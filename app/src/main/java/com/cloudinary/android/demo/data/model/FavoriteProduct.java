package com.cloudinary.android.demo.data.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Created by Nitzan Jaitman on 17/04/2018.
 */
@Entity
public class FavoriteProduct {
    @PrimaryKey
    private Integer productId;

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }
}
