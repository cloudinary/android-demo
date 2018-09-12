package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.AboutFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 11/03/2018.
 */
@FragmentScope
@Subcomponent(modules = AboutModule.class)
public interface AboutComponent extends AndroidInjector<AboutFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<AboutFragment> {
    }
}