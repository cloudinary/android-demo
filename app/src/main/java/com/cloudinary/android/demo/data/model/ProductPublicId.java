package com.cloudinary.android.demo.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

@Entity(primaryKeys = {"productId", "publicId"})
public class ProductPublicId {

    @NonNull
    String publicId;
    long productId;

    public ProductPublicId(long productId, @NonNull String publicId) {
        this.productId = productId;
        this.publicId = publicId;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @NonNull
    public String getPublicId() {
        return publicId;
    }

    public void setPublicId(@NonNull String publicId) {
        this.publicId = publicId;
    }
}
