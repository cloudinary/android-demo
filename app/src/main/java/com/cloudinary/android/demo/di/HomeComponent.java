package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.HomeFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 07/03/2018.
 */

@FragmentScope
@Subcomponent(modules = HomeModule.class)
public interface HomeComponent extends AndroidInjector<HomeFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<HomeFragment> {
    }
}