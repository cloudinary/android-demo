package com.cloudinary.android.demo.viewmodel;

import com.cloudinary.android.demo.data.ProductRepo;
import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */
@Singleton
public class ProductViewModel extends ViewModel {
    private final ProductRepo productRepo;
    private final Executor executor;

    private final MutableLiveData<Integer> productIdInput = new MutableLiveData<>();
    private final MediatorLiveData<ProductWithData> product;

    @Inject
    public ProductViewModel(ProductRepo productRepo, Executor executor) {
        this.productRepo = productRepo;
        this.executor = executor;
        product = new MediatorLiveData<>();
        product.addSource(productIdInput, this::handleProductChange);
    }

    private void handleProductChange(int productId) {
        executor.execute(() -> product.postValue(productRepo.getProductById(productId)));
    }

    public void selectProduct(int productId) {
        productIdInput.setValue(productId);
    }

    public LiveData<ProductWithData> getProduct() {
        return product;
    }
}
