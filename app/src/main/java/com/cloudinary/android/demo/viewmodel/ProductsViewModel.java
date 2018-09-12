package com.cloudinary.android.demo.viewmodel;

import com.cloudinary.android.demo.data.ProductRepo;
import com.cloudinary.android.demo.data.model.Category;
import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Singleton;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */
@Singleton
public class ProductsViewModel extends ViewModel {
    private static final String TAG = ProductsViewModel.class.getSimpleName();
    private final LiveData<List<ProductWithData>> allProducts;
    private final LiveData<List<Category>> allCategories;
    private final MutableLiveData<ProductsFilter> productsFilterInput = new MutableLiveData<>();
    private final MediatorLiveData<List<ProductWithData>> selectedProducts;
    private final MediatorLiveData<CategoriesWrapper> selectedCategories;
    ProductRepo productRepo;

    @Inject
    public ProductsViewModel(ProductRepo productRepo) {
        this.productRepo = productRepo;

        allCategories = productRepo.getAllCategories();
        allProducts = productRepo.getAllProducts();

        selectedProducts = new MediatorLiveData<>();
        selectedProducts.addSource(productsFilterInput, productsFilter -> handleProductsChange());
        selectedProducts.addSource(allProducts, productWithData -> handleProductsChange());

        selectedCategories = new MediatorLiveData<>();
        selectedCategories.addSource(productsFilterInput, productsFilter -> handleCategoryChange());
        selectedCategories.addSource(allCategories, productWithData -> handleCategoryChange());
    }

    private void handleCategoryChange() {
        if (productsFilterInput.getValue() != null && allCategories.getValue() != null) {
            ProductsFilter filter = productsFilterInput.getValue();
            List<Category> categories = allCategories.getValue().stream()
                    .filter(category -> filter == null || category.getDepartments().contains(filter.department))
                    .sorted(Comparator.comparingInt(o -> -o.getCountMen() - o.getCountWomen()))
                    .collect(Collectors.toList());

            selectedCategories.setValue(new CategoriesWrapper(categories, getSelectedDepartment()));
        }
    }

    private void handleProductsChange() {
        if (productsFilterInput.getValue() != null && allProducts.getValue() != null) {
            ProductsFilter filter = productsFilterInput.getValue();
            selectedProducts.setValue(allProducts.getValue().stream()
                    .filter(product -> filter(filter, product))
                    .collect(Collectors.toList()));
        }
    }

    public MutableLiveData<ProductsFilter> getProductsFilterInput() {
        return productsFilterInput;
    }

    private boolean filter(ProductsFilter filter, ProductWithData product) {
        if (filter == null || (filter.category == null && filter.department == null)) {
            return true;
        } else if (filter.category != null) {
            if (filter.department != null) {
                return product.getTags().stream().anyMatch(tag -> tag.getTag().equals(filter.category)) && product.getProduct().getDepartment().equals(filter.department);
            } else {
                return product.getTags().stream().anyMatch(tag -> tag.getTag().equals(filter.category));
            }
        } else {
            return product.getProduct().getDepartment().equals(filter.department);
        }
    }

    public LiveData<List<ProductWithData>> getSelectedProducts() {
        return selectedProducts;
    }

    public LiveData<List<Category>> getCategories() {
        return allCategories;
    }

    public void setFilter(ProductsFilter filter) {
        productsFilterInput.setValue(filter);
    }

    public void setDepartment(String department) {
        productsFilterInput.setValue(new ProductsFilter(department, getSelectedCategory()));
    }

    public void setCategory(String category) {
        productsFilterInput.setValue(new ProductsFilter(getSelectedDepartment(), category));
    }

    public LiveData<CategoriesWrapper> getSelectedCategories() {
        return selectedCategories;
    }

    public String getSelectedDepartment() {
        return productsFilterInput.getValue() == null ? null : productsFilterInput.getValue().department;
    }

    public String getSelectedCategory() {
        return productsFilterInput.getValue() == null ? null : productsFilterInput.getValue().category;
    }

    public LiveData<List<ProductWithData>> getProducts() {
        return allProducts;
    }

    public void updateFavoriteState(ProductWithData productWithData, boolean newIsFavorite) {
        if (productWithData.isFavorite() == newIsFavorite) {
            // nothing to do
            return;
        }

        if (newIsFavorite) {
            productRepo.addToFavorites(productWithData);
        } else {
            productRepo.removeFromFavorites(productWithData);
        }
    }

    public final static class CategoriesWrapper {
        public final List<Category> categories;
        public final String department;

        public CategoriesWrapper(List<Category> categories, String department) {
            this.categories = categories;
            this.department = department;
        }
    }

    public final static class ProductsFilter {
        public final String department;
        public final String category;

        public ProductsFilter(String department, String category) {
            this.department = department;
            this.category = category;
        }
    }

}
