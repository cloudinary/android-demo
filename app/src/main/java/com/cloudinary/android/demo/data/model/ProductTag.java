package com.cloudinary.android.demo.data.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

@Entity(primaryKeys = {"productId", "tag"})
public class ProductTag {

    long productId;
    @NonNull
    String tag;

    public ProductTag(long productId, @NonNull String tag) {
        this.productId = productId;
        this.tag = tag;
    }

    public long getProductId() {
        return productId;
    }

    public void setProductId(long productId) {
        this.productId = productId;
    }

    @NonNull
    public String getTag() {
        return tag;
    }

    public void setTag(@NonNull String tag) {
        this.tag = tag;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductTag)) return false;

        ProductTag tag1 = (ProductTag) o;

        if (productId != tag1.productId) return false;
        return tag.equals(tag1.tag);
    }

    @Override
    public int hashCode() {
        int result = (int) (productId ^ (productId >>> 32));
        result = 31 * result + tag.hashCode();
        return result;
    }
}
