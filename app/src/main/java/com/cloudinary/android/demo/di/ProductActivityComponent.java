package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */
@Subcomponent(modules = ProductActivityModule.class)
public interface ProductActivityComponent extends AndroidInjector<ProductActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ProductActivity> {
    }
}
