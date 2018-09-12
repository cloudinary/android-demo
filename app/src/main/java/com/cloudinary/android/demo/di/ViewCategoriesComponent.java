package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.CategoriesFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

@FragmentScope
@Subcomponent(modules = ViewCategoriesModule.class)
public interface ViewCategoriesComponent extends AndroidInjector<CategoriesFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<CategoriesFragment> {
    }
}

