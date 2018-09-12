package com.cloudinary.android.demo.data.model;

import com.cloudinary.android.demo.app.Identifiable;

import java.util.Set;

import androidx.room.Embedded;
import androidx.room.Ignore;
import androidx.room.Relation;

/**
 * Created by Nitzan Jaitman on 08/03/2018.
 */

public class ProductWithData implements Identifiable {
    @Embedded
    Product product;

    @Relation(parentColumn = "id",
            entityColumn = "productId")
    private Set<ProductTag> tags;

    @Relation(parentColumn = "id",
            entityColumn = "productId")
    private Set<ProductPublicId> images;

    @Ignore
    private boolean favorite;

    public ProductWithData() {
    }

    @Ignore
    public ProductWithData(ProductWithData src, boolean favorite) {
        this.product = src.getProduct();
        this.tags = src.getTags();
        this.images = src.getImages();
        this.favorite = favorite;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public Set<ProductTag> getTags() {
        return tags;
    }

    public void setTags(Set<ProductTag> tags) {
        this.tags = tags;
    }

    public Set<ProductPublicId> getImages() {
        return images;
    }

    public void setImages(Set<ProductPublicId> images) {
        this.images = images;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductWithData)) return false;

        ProductWithData that = (ProductWithData) o;

        if (favorite != that.favorite) return false;
        if (!product.equals(that.product)) return false;
        if (!tags.equals(that.tags)) return false;
        return images.equals(that.images);
    }

    @Override
    public int hashCode() {
        int result = product.hashCode();
        result = 31 * result + tags.hashCode();
        result = 31 * result + images.hashCode();
        result = 31 * result + (favorite ? 1 : 0);
        return result;
    }

    @Override
    public String getId() {
        return String.valueOf(product.getId());
    }

    public boolean isFavorite() {
        return favorite;
    }

    public ProductWithData copy() {
        return this;
    }
}
