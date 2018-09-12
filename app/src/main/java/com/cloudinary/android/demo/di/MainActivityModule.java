package com.cloudinary.android.demo.di;


import com.cloudinary.android.demo.app.AboutFragment;
import com.cloudinary.android.demo.app.CartFragment;
import com.cloudinary.android.demo.app.FavoritesFragment;
import com.cloudinary.android.demo.app.HomeFragment;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

@Module(subcomponents = {HomeComponent.class, AboutComponent.class, CartComponent.class, FavoritesComponent.class})
public abstract class MainActivityModule {
    @Binds
    @IntoMap
    @FragmentKey(HomeFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideHomeComponent(HomeComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(AboutFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideAboutComponent(AboutComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(CartFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideCartComponent(CartComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(FavoritesFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideFavoritesComponent(FavoritesComponent.Builder builder);
}