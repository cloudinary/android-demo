package com.cloudinary.android.demo.data.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */

@Entity
public class CartItem {
    @PrimaryKey
    private Integer productId;
    private int count;
    private long updatedTimeStamp;

    public CartItem() {
    }

    @Ignore
    public CartItem(Integer productId, int count, long updatedTimeStamp) {
        this.productId = productId;
        this.count = count;
        this.updatedTimeStamp = updatedTimeStamp;
    }

    public Integer getProductId() {
        return productId;
    }

    public void setProductId(Integer productId) {
        this.productId = productId;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public long getUpdatedTimeStamp() {
        return updatedTimeStamp;
    }

    public void setUpdatedTimeStamp(long updatedTimeStamp) {
        this.updatedTimeStamp = updatedTimeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItem)) return false;

        CartItem cartItem = (CartItem) o;

        if (count != cartItem.count) return false;
        if (updatedTimeStamp != cartItem.updatedTimeStamp) return false;
        return productId.equals(cartItem.productId);
    }

    @Override
    public int hashCode() {
        int result = productId.hashCode();
        result = 31 * result + count;
        result = 31 * result + (int) (updatedTimeStamp ^ (updatedTimeStamp >>> 32));
        return result;
    }
}
