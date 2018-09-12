package com.cloudinary.android.demo.app;

import com.cloudinary.android.demo.data.model.ProductWithData;

import java.util.stream.Collectors;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

public class FavoritesFragment extends ProductsGridFragment {
    @Override
    protected void startObserving() {
        productsViewModel.getProducts().observe(this, products -> updateUI(products.stream().filter(ProductWithData::isFavorite).collect(Collectors.toList())));
    }
}
