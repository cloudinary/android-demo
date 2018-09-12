package com.cloudinary.android.demo.app;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.ProductTag;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.ProductViewModel;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;
import com.google.android.flexbox.FlexboxLayout;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import dagger.android.support.DaggerFragment;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

public class RelatedProductsFragment extends DaggerFragment {
    @Inject
    ViewModelProvider.Factory viewModelFactory;
    private ProductViewModel productViewModel;
    private ProductsViewModel productsViewModel;

    private FlexboxLayout tagsContainer;
    private HomeFragment.tagsClickListener listener;
    private RecyclerView recyclerView;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_related_products, container, false);
        tagsContainer = rootView.findViewById(R.id.related_viewgroup_tags_container);

        listener = getTagsClickListener();

        // Setup recycler view:
        recyclerView = rootView.findViewById(R.id.related_list_products);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        Context context = rootView.getContext();
        recyclerView.setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.addItemDecoration(new DividerItemDecoration(context, LinearLayoutManager.HORIZONTAL));

        // setup adapter:
        int itemWidth = getResources().getDimensionPixelOffset(R.dimen.related_product_item_width);
        int itemMargin = getResources().getDimensionPixelOffset(R.dimen.related_product_item_margin);
        ProductsAdapter adapter = new ProductsAdapter(context, itemWidth, itemMargin);
        adapter.setSelectListener(product -> {
            final Activity activity = getActivity();
            if (activity != null && activity instanceof ProductsAdapter.ProductSelectListener) {
                ((ProductsAdapter.ProductSelectListener) activity).onProductSelected(product);
            }
        });
        adapter.setFavoriteListener((productWithData, isFavorite) -> productsViewModel.updateFavoriteState(productWithData, isFavorite));

        // connect them:
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private HomeFragment.tagsClickListener getTagsClickListener() {
        return tag -> {
            // update the view model with the selected tag:
            productsViewModel.setFilter(new ProductsViewModel.ProductsFilter(null, tag));
            FragmentActivity activity = getActivity();

            // switch to new activity:
            if (activity instanceof CategoriesFragment.CategoriesActivityInterface) {
                ((CategoriesFragment.CategoriesActivityInterface) activity).onCategorySelected(tag, null, null);
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        productViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductViewModel.class);
        productsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductsViewModel.class);
        productViewModel.getProduct().observe(this, this::setupRelatedProductsList);
    }

    private void setupRelatedProductsList(ProductWithData productWithData) {
        ActivityUtils.updateCategories(getActivity(), productWithData.getTags().stream().map(ProductTag::getTag).collect(Collectors.toList()), tagsContainer, listener);
        productsViewModel.getProducts().observe(this, products -> setupRelatedProductsList(productWithData, products));
    }

    private void setupRelatedProductsList(ProductWithData selected, List<ProductWithData> productsWithData) {
        Set<String> selectedTags = selected.getTags().stream().map(ProductTag::getTag).collect(Collectors.toSet());
        List<ProductWithData> related = productsWithData.stream().filter(product ->
                !product.getProduct().getId().equals(selected.getProduct().getId()) && product.getTags().stream().anyMatch(tag -> selectedTags.contains(tag.getTag())))
                .collect(Collectors.toList());
        ((ProductsAdapter) recyclerView.getAdapter()).setItems(related);
    }
}