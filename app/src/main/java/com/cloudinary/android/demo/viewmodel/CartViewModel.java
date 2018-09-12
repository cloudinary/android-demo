package com.cloudinary.android.demo.viewmodel;

import com.cloudinary.android.demo.data.ProductRepo;
import com.cloudinary.android.demo.data.model.CartItemWithData;
import com.cloudinary.android.demo.data.model.Product;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */

@Singleton
public class CartViewModel extends ViewModel {
    private final ProductRepo productRepo;
    private final LiveData<List<CartItemWithData>> cartItems;

    @Inject
    public CartViewModel(ProductRepo productRepo) {
        this.productRepo = productRepo;
        cartItems = productRepo.getCartItems();
    }

    public LiveData<List<CartItemWithData>> getCartItems() {
        return cartItems;
    }

    public void add(Product product, int toAdd) {
        productRepo.updateProductCartCount(product, toAdd);
    }

    public void subtract(Product product, int toSubtract) {
        productRepo.updateProductCartCount(product, -toSubtract);
    }
}
