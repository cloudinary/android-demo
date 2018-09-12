package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.CategoriesActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */
@Subcomponent(modules = CategoriesActivityModule.class)
public interface CategoriesActivityComponent extends AndroidInjector<CategoriesActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<CategoriesActivity> {
    }
}
