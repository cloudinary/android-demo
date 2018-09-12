package com.cloudinary.android.demo.remote;

import com.cloudinary.android.demo.data.ProductRepo;
import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.List;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

/**
 * This interface defines the required behaviours of a remote product repository. An implementation
 * of this interface is injected to the {@link ProductRepo}
 */
public interface RemoteProductRepo {
    /**
     * Get all the products from the remote repository
     *
     * @param userToken The user requesting the products. In a real app this should
     *                  be used to identify this user's products and/or authenticate, if applies.
     * @return The list of products, with all their aggregated data (tags, image urls).
     */
    List<ProductWithData> getAllProducts(String userToken);

    /**
     * Add a new product to the remtoe repository
     *
     * @param userToken   The user uploading the resource, for validation/authentication
     * @param imageUris   Local image URIs of the product
     * @param name        Product name
     * @param department  Product department (women/men)
     * @param description Product description
     * @param price       Product price
     * @param category    Product category
     * @param callback    Callback for progress and upload events
     */
    void addProduct(String userToken, List<String> imageUris, String name, String department, String description, int price, String category, AddProductEvents callback);

    /**
     * Interface definition for upload events callbacks
     */
    interface AddProductEvents {
        /**
         * Product uploaded started
         */
        void onStart();

        /**
         * Error in product upload
         */
        void onError();

        /**
         * Product upload finished
         */
        void onFinished();

        /**
         * Product upload progress (percent value)
         *
         * @param progress
         */
        void onProgress(float progress);
    }
}