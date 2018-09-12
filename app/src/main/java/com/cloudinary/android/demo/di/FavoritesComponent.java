package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.FavoritesFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

@FragmentScope
@Subcomponent(modules = FavoritesModule.class)
public interface FavoritesComponent extends AndroidInjector<FavoritesFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<FavoritesFragment> {
    }
}
