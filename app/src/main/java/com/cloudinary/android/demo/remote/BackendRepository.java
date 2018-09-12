package com.cloudinary.android.demo.remote;

import android.net.Uri;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductPublicId;
import com.cloudinary.android.demo.data.model.ProductTag;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.data.model.RemoteProduct;
import com.cloudinary.android.demo.di.AppModule;
import com.cloudinary.android.demo.util.ConfigurationProvider;
import com.cloudinary.android.policy.TimeWindow;
import com.cloudinary.android.policy.UploadPolicy;
import com.cloudinary.utils.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * A simulated remote repo implementation. This can be used instead of cloudinary as
 * a backend (see {@link AppModule#provideBackendRepo App module injections}
 * In this implementation, cloudinary is used solely as a media backend, while the product data
 * itself (excluding the images) sits in our own backend.
 */
@Singleton
public class BackendRepository implements RemoteProductRepo {
    private final BackendWebService webService;
    private final ConfigurationProvider configurationProvider;

    @Inject
    public BackendRepository(BackendWebService webService, ConfigurationProvider configurationProvider) {
        this.webService = webService;
        this.configurationProvider = configurationProvider;
    }

    /**
     * {@inheritDoc}
     */
    public List<ProductWithData> getAllProducts(String userToken) {
        // get the data from our backend and adapt to local model (RemoteProduct -> ProductWithData)
        // The data contains the Cloudinary image urls for each product (later fetched for the UI).
        List<ProductWithData> result = new ArrayList<>();
        BackendWebService.BackendProductsResult allProducts = webService.getAllProducts();
        result.addAll(allProducts.originals.stream().map(product -> adapt(userToken, configurationProvider.getCloudName(), product)).collect(Collectors.toList()));
        result.addAll(allProducts.userUploads.stream().map(product -> adapt(userToken, configurationProvider.getSharedCloudName(), product)).collect(Collectors.toList()));
        return result;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addProduct(String userToken, List<String> imageUris, String name, String department, String description, int price, String category, AddProductEvents callback) {

//         ***NOTE ***
//        In a real app, the best approach is to provide a notification url (webhook) to cloudinary
//        when uploading resources. This way, your backend is automatically updated when the images
//        finish uploading.The approach is demonstrated here, however since it's not a real backend
//        we don 't actually use it, but rather trust the app itself to notify the backend (not
//        a very good practice in the real world).


        // Step 1: Upload the images to cloudinary
        List<String> nonBlankImages = imageUris.stream().filter(StringUtils::isNotBlank).collect(Collectors.toList());

        // Use a callback aggregator to only get notified on the entire upload process, ignoring
        // specific notifications for each image uploaded.
        CallbackAggregator callbackAggregator = new CallbackAggregator(callback, nonBlankImages, publicIds -> {
            // Step 2: after all the images are uploaded, use the upload result (public ids) and add
            // the product to your backend *** NOTE THE COMMENT ABOVE - this should
            // not be necessary in a real app, as the webhook takes care of it - a simialr
            // code should run in your backend, saving the new data, and then notifying this app
            // to refresh it's data (or let the app poll for updates after uploading images).
            RemoteProduct product = new RemoteProduct();
            product.setMainImage(publicIds.get(0));
            product.setName(name);
            product.setDepartment(department);
            product.setDescription(description);
            product.setPrice(price);

            product.setTags(new ArrayList<>());
            product.getTags().add(category);

            // mark this product as belonging to this user - in a real world app the relation
            // between user and resource may be different (e.g. not go through tags).
            product.getTags().add(userToken);
            product.setImages(new ArrayList<>(publicIds));
            webService.saveProduct(product);
        });

        for (String image : nonBlankImages) {
            MediaManager.get().upload(Uri.parse(image))
                    .unsigned("user_upload")
//                    *** NOTE *** - uncomment this option and provide a real url for employing
//                    the notification url mechanism:
//                    .option("notification_url", "https://your-backend.com/cld_notify")
                    .constrain(TimeWindow.immediate())
                    // this should be a short operation when the app is active, no need for retries
                    .policy(new UploadPolicy.Builder().maxRetries(0).build())
                    .callback(callbackAggregator)
                    .dispatch();
        }
    }

    private ProductWithData adapt(String userToken, String cloudName, RemoteProduct remoteProduct) {
        ProductWithData productWithData = new ProductWithData();
        Product product = new Product();
        product.setId(remoteProduct.getId());
        product.setDescription(remoteProduct.getDescription());
        product.setName(remoteProduct.getName());
        product.setPrice(remoteProduct.getPrice());
        product.setMainImage(remoteProduct.getMainImage());
        product.setDepartment(remoteProduct.getDepartment());
        product.setCloud(cloudName);
        productWithData.setProduct(product);
        productWithData.setImages(new HashSet<>(remoteProduct.getImages().size()));
        productWithData.setTags(new HashSet<>(remoteProduct.getTags().size()));

        remoteProduct.getImages().forEach(image -> productWithData.getImages().add(new ProductPublicId(product.getId(), image)));
        // since we used tags to store user token, we filter it out when constructing local models:
        remoteProduct.getTags().stream().filter(tag -> !tag.equals(userToken)).forEach(tag -> productWithData.getTags().add(new ProductTag(product.getId(), tag)));
        return productWithData;
    }

    // Not in use but may be useful in the future
    private RemoteProduct adapt(ProductWithData productWithData) {
        RemoteProduct product = new RemoteProduct();
        product.setId(productWithData.getProduct().getId());
        product.setMainImage(productWithData.getProduct().getMainImage());
        product.setName(productWithData.getProduct().getName());
        product.setDepartment(productWithData.getProduct().getDepartment());
        product.setDescription(productWithData.getProduct().getDescription());
        product.setPrice(productWithData.getProduct().getPrice());
        product.setSizes(productWithData.getProduct().getSizes());
        product.setTags(new ArrayList<>(productWithData.getTags().size()));
        productWithData.getTags().forEach(tag -> product.getTags().add(tag.getTag()));

        product.setImages(new ArrayList<>(productWithData.getImages().size()));
        productWithData.getImages().forEach(image -> product.getImages().add(image.getPublicId()));

        return product;
    }
}
