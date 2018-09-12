package com.cloudinary.android.demo.data;


import android.util.Log;

import com.cloudinary.android.demo.data.meta.Prefs;
import com.cloudinary.android.demo.data.model.CartItem;
import com.cloudinary.android.demo.data.model.CartItemWithData;
import com.cloudinary.android.demo.data.model.Category;
import com.cloudinary.android.demo.data.model.FavoriteProduct;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductTag;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.di.AppModule;
import com.cloudinary.android.demo.remote.BackendWebService;
import com.cloudinary.android.demo.remote.RemoteProductRepo;
import com.cloudinary.android.demo.util.ConfigurationProvider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.Transformations;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

/**
 * This is the main and *only* entry point for data in the app. Viewmodels should never access
 * any other data interface directly.
 */
@Singleton
public class ProductRepo {
    private static final String TAG = ProductRepo.class.getSimpleName();

    private final Executor executor;
    private final ProductDao productDao;
    private final RemoteProductRepo remoteRepo;
    private final Object remoteFetchSyncObject = new Object();
    private final Prefs prefs;
    private boolean fresh = false;

    /**
     * Construct a new instance of ProductRepo. This is done by dagger. Notice the named
     * {@link RemoteProductRepo} - This can be either `cloudinary` or `backend` to demonstrate
     * using Cloudinary both as a full backend or just as a media service with your own server as
     * the backend. See {@link AppModule#provideBackendRepo(BackendWebService, ConfigurationProvider)} backend repo provider}.
     *
     * @param executor
     * @param productDao
     * @param remoteRepo
     */
    @Inject
    ProductRepo(Executor executor, ProductDao productDao, @Named("cloudinary") RemoteProductRepo remoteRepo, Prefs prefs) {
        this.executor = executor;
        this.productDao = productDao;
        this.remoteRepo = remoteRepo;
        this.prefs = prefs;

        // fetch new data upon creation
        refreshData();
    }

    /**
     * Fetch a live data list of all the app's categories. This is built dynamically from the
     * different products in the database
     *
     * @return The category list, as live data.
     */
    public LiveData<List<Category>> getAllCategories() {
        // Note: category is a transient model built from persistent models:
        return Transformations.map(productDao.getAllProductsWithData(), products -> {
            Map<String, Category> categoriesMap = new HashMap<>();
            for (ProductWithData product : products) {
                for (ProductTag tag : product.getTags()) {
                    String key = tag.getTag();
                    Category category = categoriesMap.computeIfAbsent(key, Category::new);
                    buildCategory(product, category);
                }
            }

            return new ArrayList<>(categoriesMap.values());
        });
    }

    private void buildCategory(ProductWithData product, Category category) {
        if (product.getProduct().getDepartment().equals("men")) {
            if (category.getPublicIdMen() == null) {
                category.setPublicIdMen(product.getProduct().getMainImage());
            }
        } else {
            if (category.getPublicIdWomen() == null) {
                category.setPublicIdWomen(product.getProduct().getMainImage());
            }
        }

        category.addDepartment(product.getProduct().getDepartment());
        category.setCloud(product.getProduct().getCloud());
    }

    /**
     * Get a single product by id.
     *
     * @param productId The id of the product to fetch
     * @return The product
     */
    public ProductWithData getProductById(int productId) {
        return productDao.getProductById(productId);
    }

    /**
     * Get all the products from the database, as live data.
     *
     * @return The products list.
     */
    public LiveData<List<ProductWithData>> getAllProducts() {
        MediatorLiveData<List<ProductWithData>> allProductsWithFavoriteData = new MediatorLiveData<>();

        LiveData<List<FavoriteProduct>> favoriteProductIds = productDao.getFavoriteIds();
        LiveData<List<ProductWithData>> rawAll = productDao.getAllProductsWithData();

        allProductsWithFavoriteData.addSource(favoriteProductIds, products -> buildProductsWithFavData(allProductsWithFavoriteData, rawAll, favoriteProductIds));
        allProductsWithFavoriteData.addSource(rawAll, products -> buildProductsWithFavData(allProductsWithFavoriteData, rawAll, favoriteProductIds));

        return allProductsWithFavoriteData;
    }

    // once both favorite and full products list is loaded we merge them into allProductsWithFavoriteData livedata which
    // will trigger the selected products (if observed).
    private void buildProductsWithFavData(MediatorLiveData<List<ProductWithData>> aggregated, LiveData<List<ProductWithData>> allProducts, LiveData<List<FavoriteProduct>> favoriteProductIds) {
        if (favoriteProductIds.getValue() != null && allProducts.getValue() != null) {
            // fetch all ids of favorite products:
            Set<Integer> favoriteIds = favoriteProductIds.getValue().stream()
                    .map(FavoriteProduct::getProductId)
                    .collect(Collectors.toSet());

            // Build the aggregated list with the favorite data included
            List<ProductWithData> aggregatedData = allProducts.getValue().stream()
                    .map(src -> new ProductWithData(src, favoriteIds.contains(src.getProduct().getId())))
                    .sorted((o1, o2) -> Long.compare(o2.getProduct().getTimestamp(), o1.getProduct().getTimestamp()))
                    .collect(Collectors.toList());

            // set the value (and trigger the live data update)
            aggregated.setValue(aggregatedData);
        }
    }

    private void refreshData() {
        if (fresh) {
            return;
        }

        Log.d(TAG, "Product Repo refreshing data...");
        executor.execute(this::doSynchronization);
    }

    /**
     * Add a new product to the app. This creates a new product in the remote products service and
     * then fetches the updated list and syncs the local DB.
     *
     * @param images      list of local images for the product.
     * @param name        Product name
     * @param department  Product department (men/women)
     * @param description Product description
     * @param price       Product price
     * @param category    Product category
     * @param callback    Callback for product upload progress and events.
     */
    public void addProduct(List<String> images, String name, String department, String description, int price, String category, RemoteProductRepo.AddProductEvents callback) {
        // Add this product, marking it as 'belonging' to this user. This should be validated
        // in a real world app (e.g. backend service should authenticate user).
        remoteRepo.addProduct(prefs.getUserToken(), images, name, department, description, price, category.toLowerCase(), new RemoteProductRepo.AddProductEvents() {
            @Override
            public void onStart() {
                callback.onStart();
            }

            @Override
            public void onError() {
                callback.onError();
            }

            @Override
            public void onFinished() {
                callback.onFinished();
                fresh = false;
                refreshData();
            }

            @Override
            public void onProgress(float progress) {
                callback.onProgress(progress);
            }
        });
    }

    private void doSynchronization() {
        synchronized (remoteFetchSyncObject) {
            if (fresh) {
                return;
            }

            List<ProductWithData> products = remoteRepo.getAllProducts(prefs.getUserToken());
            if (products != null) {
                productDao.syncProducts(products);
                fresh = true;
                Log.d(TAG, "Product Repo refreshing data...DONE!");
            }
        }
    }

    /**
     * Get all the cart items
     *
     * @return The cart items as live data.
     */
    public LiveData<List<CartItemWithData>> getCartItems() {
        return productDao.getCart();
    }

    /**
     * Update cart item product count
     *
     * @param product The product to update count for
     * @param delta   How many products to add/remove (can be negative)
     */
    public void updateProductCartCount(Product product, int delta) {
        executor.execute(() -> {
            // fetch existing state:
            CartItem cartItem = productDao.getCartItemForProductId(product.getId());
            int newCount = cartItem == null ? delta : cartItem.getCount() + delta;
            if (newCount < 1) {
                productDao.deleteCartForProductId(product.getId());
            } else {
                long updatedTimeStamp = System.currentTimeMillis();
                if (cartItem == null) {
                    cartItem = new CartItem(product.getId(), newCount, updatedTimeStamp);
                    productDao.insert(cartItem);
                } else {
                    cartItem.setCount(newCount);
                    cartItem.setUpdatedTimeStamp(updatedTimeStamp);
                    productDao.update(cartItem);
                }
            }
        });
    }

    /**
     * Add a product to the favorites list.
     *
     * @param productWithData The product to add
     */
    public void addToFavorites(ProductWithData productWithData) {
        FavoriteProduct fav = new FavoriteProduct();
        fav.setProductId(productWithData.getProduct().getId());
        executor.execute(() -> productDao.addToFavorite(fav));
    }

    /**
     * Remvoe a product from the favorites list.
     *
     * @param productWithData
     */
    public void removeFromFavorites(ProductWithData productWithData) {
        executor.execute(() -> productDao.removeFromFavorites(productWithData.getProduct().getId()));
    }
}
