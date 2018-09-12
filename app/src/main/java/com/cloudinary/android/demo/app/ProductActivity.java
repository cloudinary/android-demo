package com.cloudinary.android.demo.app;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.ProductViewModel;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

public class ProductActivity extends DaggerAppCompatActivity implements ProductFragment.GoToCartListener, CategoriesFragment.CategoriesActivityInterface, ProductsAdapter.ProductSelectListener {
    public static final String PRODUCT_ID_INTENT_EXTRA = "PRODUCT_ID_INTENT_EXTRA";
    private static final String PRODUCT_FRAGMENT_TAG = "PRODUCT_FRAGMENT_TAG";
    private static final String RELATED_PRODUCTS_FRAGMENT_TAG = "RELATED_PRODUCTS_FRAGMENT_TAG";

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    @SuppressWarnings("FieldCanBeLocal") // for clarity, view models are fields
    private ProductViewModel productViewModel;
    private Toolbar toolbar;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);

        // Init toolbar:
        toolbar = findViewById(R.id.product_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);


        // Add the fragments:
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction()
                .add(R.id.product_viewgroup_container, new ProductFragment(), PRODUCT_FRAGMENT_TAG)
                .add(R.id.product_related_container, new RelatedProductsFragment(), RELATED_PRODUCTS_FRAGMENT_TAG)
                .commit();

        // get view model and observe changes:
        int productId = getIntent().getIntExtra(PRODUCT_ID_INTENT_EXTRA, -1);
        productViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProductViewModel.class);
        productViewModel.selectProduct(productId);
        productViewModel.getProduct().observe(this, this::updateTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateTitle(ProductWithData productWithData) {
        toolbar.setTitle(productWithData.getProduct().getName());
    }

    @Override
    public void onGoToCart() {
        startActivity(new Intent(this, MainActivity.class).putExtra(MainActivity.INTENT_EXTRA_TAB, MainActivity.INTENT_EXTRA_TAB_CART));
    }

    @Override
    public void onCategorySelected(String tag, View imageView, View view) {
        startActivity(new Intent(this, ProductsActivity.class));
    }

    @Override
    public void onProductSelected(Product product) {
        Intent intent = new Intent(this, ProductActivity.class).putExtra(ProductActivity.PRODUCT_ID_INTENT_EXTRA, product.getId());
        startActivity(intent);

    }
}
