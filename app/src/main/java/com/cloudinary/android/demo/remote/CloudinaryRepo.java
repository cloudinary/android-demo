package com.cloudinary.android.demo.remote;

import android.annotation.SuppressLint;
import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.demo.data.meta.Prefs;
import com.cloudinary.android.demo.data.model.CloudinaryJsonResponse;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductPublicId;
import com.cloudinary.android.demo.data.model.ProductTag;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.util.ConfigurationProvider;
import com.cloudinary.android.demo.util.Utils;
import com.cloudinary.android.policy.TimeWindow;
import com.cloudinary.android.policy.UploadPolicy;
import com.cloudinary.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import retrofit2.Response;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

/**
 * Access point for cloudinary as backend - list the resources and upload new products.
 * In this repo we use Cloudinary as our full backend solution - we upload all the data
 * (resource+meta) to cloudinary.
 * <p>
 * When adding a new product we attach all the info to the resource itself, employing tags and
 * context (key-value pairs) to save all the product details and user meta data.
 * <p>
 * When fetching products, we list all the resources per tag, reading all the metadata
 * from them and then merging into product model
 */
@Singleton
public class CloudinaryRepo implements RemoteProductRepo {

    private final Prefs prefs;
    private final ConfigurationProvider configurationProvider;
    private CloudinaryWebService webService;

    @Inject
    public CloudinaryRepo(CloudinaryWebService webService, Prefs prefs, ConfigurationProvider configurationProvider) {
        this.webService = webService;
        this.prefs = prefs;
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     *
     * @param userToken
     */
    public List<ProductWithData> getAllProducts(String userToken) {
        try {
            List<ProductWithData> result = new ArrayList<>();
            // we fetch two lists from cloudinary - the general tag for all the 'global' products
            // and another for this user's specific tag, to get all this user's uploads.
            long version = System.currentTimeMillis();
            Response<CloudinaryJsonResponse> response = webService.getProducts(configurationProvider.getCloudName(), configurationProvider.getGlobalTag(), version).execute();
            CloudinaryJsonResponse body = response.body();
            if (response.isSuccessful() && body != null) {
                result.addAll(adapt(userToken, configurationProvider.getCloudName(), response.body().getResources()));
            }

            String customTag = prefs.getUserToken();
            if (StringUtils.isNotBlank(customTag)) {
                response = webService.getProducts(configurationProvider.getSharedCloudName(), customTag, version).execute();
                body = response.body();
                if (response.isSuccessful() && body != null) {
                    result.addAll(adapt(userToken, configurationProvider.getSharedCloudName(), response.body().getResources()));
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProduct(String userToken, List<String> imageUris, String name, String department, String description, int price, String category, AddProductEvents callback) {
        // Here we attach the full product data to each resource, so later we can use this data
        // to construct the full products list.
        Map<String, String> context = Utils.asMap("price", String.valueOf(price), "name", name, "department", department, "description", description);
        context.put("productId", String.valueOf(new Random().nextInt()));

        List<String> tags = new ArrayList<>();
        tags.add(category);

        // Add the user token to the resource tags, so it can be later identified and retrieved
        // for this user ,see getAllProducts().
        tags.add(userToken);

        List<String> nonBlankImages = imageUris.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());
        CallbackAggregator callbackAggregator = new CallbackAggregator(callback, nonBlankImages, null);

        boolean first = true;
        for (String image : nonBlankImages) {
            if (first) {
                context.put("isMain", "true");
            } else {
                context.remove("isMain");
            }

            MediaManager.get().upload(Uri.parse(image))
                    .unsigned("user_upload")
                    .option("tags", tags)
                    .option("context", context).callback(callbackAggregator)
                    .constrain(TimeWindow.immediate())
                    // this should be a short operation when the app is active, no need for retries
                    .policy(new UploadPolicy.Builder().maxRetries(0).build())
                    .dispatch();

            first = false;
        }
    }

    /**
     * Adapt the result received from Cloudinary to the local products class
     * The details of each product is stored in every product photo - we read all the data
     * and aggregate it to a single model per product.
     *
     * @param userToken The user owning the resources. We need it to clean up some metadata we
     *                  used to identify the user (but we don't want it displayed).
     * @param resources The remote resource list, to adapt to local model
     * @return A list of ProductWithData based on the remote data.
     */
    private List<ProductWithData> adapt(String userToken, String cloud, List<CloudinaryJsonResponse.RemoteResource> resources) {
        @SuppressLint("UseSparseArrays") // Sparse arrays don't support the API we use here
        final Map<Integer, ProductWithData> products = new HashMap<>();
        for (CloudinaryJsonResponse.RemoteResource resource : resources) {
            int productId = resource.getContext().getCustom().getProductId();
            ProductWithData product = products.computeIfAbsent(productId, this::buildProduct);
            ProductPublicId publicId = new ProductPublicId(productId, resource.getPublicId());
            product.getImages().add(publicId);
            if (resource.getContext().getCustom().getIsMain()) {
                product.getProduct().setMainImage(resource.getPublicId());
            }

            product.getProduct().setDepartment(resource.getContext().getCustom().getDepartment());
            product.getProduct().setDescription(resource.getContext().getCustom().getDescription());
            product.getProduct().setPrice(resource.getContext().getCustom().getPrice());
            product.getProduct().setName(resource.getContext().getCustom().getName());
            product.getProduct().setSizes(resource.getContext().getCustom().getSizes());
            product.getProduct().setCloud(cloud);

            // The user token is stored in cloudinary as part of the resource metadata, however
            // we don't want to display it or use it as an actual tag/category
            resource.getTags().stream()
                    .filter(s -> !configurationProvider.getGlobalTag().equals(s) && !userToken.equals(s))
                    .forEach(tag -> product.getTags().add(new ProductTag(productId, tag)));
        }

        return new ArrayList<>(products.values());
    }

    @NonNull
    private ProductWithData buildProduct(Integer id) {
        ProductWithData productWithData = new ProductWithData();
        Product product = new Product();
        product.setId(id);
        productWithData.setTags(new HashSet<>());
        productWithData.setImages(new HashSet<>());
        productWithData.setProduct(product);
        return productWithData;
    }
}
