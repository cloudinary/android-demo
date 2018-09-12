package com.cloudinary.android.demo.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.ProductWithData;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;

import java.util.List;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;
import dagger.android.support.DaggerFragment;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

public class ProductsGridFragment extends DaggerFragment {

    @Inject
    ViewModelProvider.Factory viewModelFactory;

    ProductsViewModel productsViewModel;

    private RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_products_grid, container, false);

        // setup recycler view
        recyclerView = rootView.findViewById(R.id.products_list_main);
        recyclerView.setHasFixedSize(true);
        ((SimpleItemAnimator) recyclerView.getItemAnimator()).setSupportsChangeAnimations(true);
        recyclerView.setLayoutManager(new GridLayoutManager(inflater.getContext(), getResources().getInteger(R.integer.product_grid_span)));
        int dividerSize = getResources().getDimensionPixelSize(R.dimen.grid_divider_width);
        recyclerView.addItemDecoration(new GridDividerItemDecoration(dividerSize, dividerSize));

        // adapter setup:
        ProductsAdapter adapter = new ProductsAdapter(inflater.getContext(), null, null);
        adapter.setSelectListener(getProductSelectListener());
        adapter.setFavoriteListener((productWithData, isFavorite) -> productsViewModel.updateFavoriteState(productWithData, isFavorite));

        // connect them:
        recyclerView.setAdapter(adapter);

        return rootView;
    }

    private ProductsAdapter.ProductSelectListener getProductSelectListener() {
        return product -> {
            final Activity activity = getActivity();
            if (activity != null && activity instanceof ProductsAdapter.ProductSelectListener) {
                ((ProductsAdapter.ProductSelectListener) activity).onProductSelected(product);
            }
        };
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        productsViewModel = ViewModelProviders.of(getActivity(), viewModelFactory).get(ProductsViewModel.class);
        startObserving();
    }

    protected void startObserving() {
        productsViewModel.getSelectedProducts().observe(this, this::updateUI);
    }

    void updateUI(List<ProductWithData> products) {
        ((ProductsAdapter) recyclerView.getAdapter()).setItems(products);
    }
}
