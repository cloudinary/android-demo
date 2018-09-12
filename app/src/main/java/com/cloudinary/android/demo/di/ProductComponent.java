package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */
@FragmentScope
@Subcomponent(modules = {ProductModule.class})
public interface ProductComponent extends AndroidInjector<ProductFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ProductFragment> {
    }
}


