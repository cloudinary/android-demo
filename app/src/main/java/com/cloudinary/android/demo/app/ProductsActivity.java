package com.cloudinary.android.demo.app;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.cld.ImageHelper;
import com.cloudinary.android.demo.data.model.Product;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.res.ResourcesCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */

public class ProductsActivity extends DaggerAppCompatActivity implements ProductsAdapter.ProductSelectListener {
    private static final String PRODUCTS_FRAGMENT_TAG = "PRODUCTS_FRAGMENT_TAG";
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private Toolbar toolbar;
    @SuppressWarnings("FieldCanBeLocal") // for clarity, view models are fields
    private ProductsViewModel productsViewModel;
    private ImageView backgroundImage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products);

        backgroundImage = findViewById(R.id.products_image_backdrop);

        // setup toolbar:
        toolbar = findViewById(R.id.products_toolbar);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.products_collapsing_toolbar);
        Typeface font = ResourcesCompat.getFont(this, R.font.montserrat_alternates_regular);
        collapsingToolbarLayout.setExpandedTitleTypeface(font);
        collapsingToolbarLayout.setCollapsedTitleTypeface(font);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        ActivityUtils.setupCustomScrimView(findViewById(R.id.products_appbar), backgroundImage, 1f);
        ActivityUtils.setupToolbarFonts(this, collapsingToolbarLayout);

        // add relevant fragment:
        getSupportFragmentManager().beginTransaction().add(R.id.products_viewgroup_container, new ProductsGridFragment(), PRODUCTS_FRAGMENT_TAG).commit();

        // retrieve view model:
        productsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProductsViewModel.class);
        productsViewModel.getProductsFilterInput().observe(this, productsFilter -> toolbar.setTitle(productsFilter.category));
        productsViewModel.getSelectedProducts().observe(this, this::updateToolbarImage);
    }

    private void updateToolbarImage(List<ProductWithData> productWithData) {
        if (productWithData.size() == 0) {
            backgroundImage.setImageDrawable(null);
        } else {
            Product product = productWithData.get(0).getProduct();
            ImageHelper.updateProductToolbarImage(product.getMainImage(), product.getCloud(), backgroundImage);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onProductSelected(Product product) {
        Intent intent = new Intent(this, ProductActivity.class).putExtra(ProductActivity.PRODUCT_ID_INTENT_EXTRA, product.getId());
        startActivity(intent);
    }
}
