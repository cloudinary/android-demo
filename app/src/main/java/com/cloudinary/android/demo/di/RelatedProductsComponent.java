package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.RelatedProductsFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */
@FragmentScope
@Subcomponent(modules = {RelatedProductModule.class})
public interface RelatedProductsComponent extends AndroidInjector<RelatedProductsFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<RelatedProductsFragment> {
    }
}

