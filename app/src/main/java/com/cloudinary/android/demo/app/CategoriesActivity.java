package com.cloudinary.android.demo.app;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.transition.Transition;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;

import javax.inject.Inject;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import dagger.android.support.DaggerAppCompatActivity;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */

public class CategoriesActivity extends DaggerAppCompatActivity implements CategoriesFragment.CategoriesActivityInterface {
    public static final String DEPARTMENT_IMAGE_TRANSITION_NAME = "DEPARTMENT_IMAGE_TRANSITION";
    private static final String CATEGORIES_FRAGMENT_TAG = "CATEGORIES_FRAGMENT_TAG";
    public static float toolbarImageAlphaBaseline = 0.65f;
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private Toolbar toolbar;
    @SuppressWarnings("FieldCanBeLocal") // for clarity, view models are fields
    private ProductsViewModel productsViewModel;
    private ImageView backgroundImage;
    private AppBarLayout appBarLayout;
    private boolean isBarMaximized = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_categories);

        // setup the background toolbar image and handle the animation:
        backgroundImage = findViewById(R.id.categories_image_backdrop);
        backgroundImage.setTransitionName(DEPARTMENT_IMAGE_TRANSITION_NAME);
        backgroundImage.setOutlineProvider(ActivityUtils.getRoundedRectProvider(getResources().getDimensionPixelOffset(R.dimen.card_corner_radius)));
        backgroundImage.animate().alpha(toolbarImageAlphaBaseline);

        // setup toolbar layour:
        toolbar = findViewById(R.id.categories_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.categories_appbar);
        appBarLayout.addOnOffsetChangedListener((appBarLayout, verticalOffset) -> isBarMaximized = verticalOffset == 0);
        ActivityUtils.setupCustomScrimView(appBarLayout, backgroundImage, toolbarImageAlphaBaseline);
        CollapsingToolbarLayout collapsingToolbarLayout = findViewById(R.id.categories_collapsing_toolbar);
        ActivityUtils.setupToolbarFonts(this, collapsingToolbarLayout);

        // show the main fragment for this activity:
        getSupportFragmentManager().beginTransaction().add(R.id.category_viewgroup_container, new CategoriesFragment(), CATEGORIES_FRAGMENT_TAG).commit();


        Transition transition = new Fade();
        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                appBarLayout.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                appBarLayout.setVisibility(View.VISIBLE);
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }
        });
        getWindow().setEnterTransition(transition);

        // get view model and register for livedata updates:
        productsViewModel = ViewModelProviders.of(this, viewModelFactory).get(ProductsViewModel.class);
        productsViewModel.getProductsFilterInput().observe(this, this::updateToolbar);
    }

    // update the views based on the live data model updates
    private void updateToolbar(ProductsViewModel.ProductsFilter productsFilter) {
        toolbar.setTitle(productsFilter.department);
        if ("women".equals(productsFilter.department)) {
            backgroundImage.setImageResource(R.drawable.img_woman_category);
        } else {
            backgroundImage.setImageResource(R.drawable.img_men_category);
        }
    }

    @Override
    public void finishAfterTransition() {
        if (!isBarMaximized) {
            finish();
        } else {
            super.finishAfterTransition();
            backgroundImage.animate().alpha(1f);
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
    public void onCategorySelected(String tag, View imageView, View textView) {
        startActivity(new Intent(this, ProductsActivity.class));
    }
}
