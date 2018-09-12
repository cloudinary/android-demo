package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductsActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */

@Subcomponent(modules = ProductsActivityModule.class)
public interface ProductsActivityComponent extends AndroidInjector<ProductsActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ProductsActivity> {
    }
}
