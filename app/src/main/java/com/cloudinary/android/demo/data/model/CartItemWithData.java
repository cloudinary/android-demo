package com.cloudinary.android.demo.data.model;

import com.cloudinary.android.demo.app.Identifiable;

import androidx.room.Embedded;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */

public class CartItemWithData implements Identifiable {
    @Embedded
    CartItem cartItem;
    @Embedded
    Product product;

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public CartItem getCartItem() {
        return cartItem;
    }

    public void setCartItem(CartItem cartItem) {
        this.cartItem = cartItem;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof CartItemWithData)) return false;

        CartItemWithData that = (CartItemWithData) o;

        if (!cartItem.equals(that.cartItem)) return false;
        return product.equals(that.product);
    }

    @Override
    public int hashCode() {
        int result = cartItem.hashCode();
        result = 31 * result + product.hashCode();
        return result;
    }

    @Override
    public String getId() {
        return String.valueOf(cartItem.getProductId());
    }
}
