package com.cloudinary.android.demo.app;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.Category;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import dagger.android.support.DaggerFragment;

public class CategoriesFragment extends DaggerFragment {
    private static final String TAG = CategoriesFragment.class.getSimpleName();

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    private ProductsViewModel productsViewModel;
    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_categories, container, false);

        // setup recycler view
        recyclerView = rootView.findViewById(R.id.categories_list_main);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        int span = getResources().getInteger(R.integer.product_grid_span);
        StaggeredGridLayoutManager layoutManager = new StaggeredGridLayoutManager(span, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        recyclerView.setLayoutManager(layoutManager);
        int dividerWidth = getResources().getDimensionPixelSize(R.dimen.categories_grid_divider_width);
        int dividerHeight = getResources().getDimensionPixelSize(R.dimen.categories_grid_divider_height);
        recyclerView.addItemDecoration(new GridDividerItemDecoration(dividerWidth, dividerHeight));
        int itemHeight = ActivityUtils.getScreenWidth(rootView.getContext()) / span +
                (span - 1) * getResources().getDimensionPixelSize(R.dimen.categories_grid_divider_height);
        recyclerView.setAdapter(new CategoriesAdapter(rootView.getContext(), itemHeight, this::selectCategory));

        return rootView;
    }

    private void selectCategory(Category category, View imageView, View textView) {
        // update the view models with the selected category and notify listener that something
        // changed:
        String tag = category.getTag();
        productsViewModel.setCategory(tag);

        CategoriesActivityInterface activityInterface = (CategoriesActivityInterface) getActivity();
        if (activityInterface != null) {
            activityInterface.onCategorySelected(tag, imageView, textView);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // get view model and register for updates:
        productsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductsViewModel.class);
        productsViewModel.getSelectedCategories().observe(this, this::updateUI);
    }

    /**
     * Respond to update in the live data models
     *
     * @param categories The new categories (post-update)
     */
    private void updateUI(ProductsViewModel.CategoriesWrapper categories) {
        Log.d(TAG, String.format("updateUI settings categories adapter with %d items...", categories.categories.size()));
        ((CategoriesAdapter) recyclerView.getAdapter()).setItems(categories);
    }

    public interface CategoriesActivityInterface {
        void onCategorySelected(String tag, View imageView, View view);
    }
}
