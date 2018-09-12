package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.CategoriesFragment;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */

@Module(subcomponents = {ViewCategoriesComponent.class})
public abstract class CategoriesActivityModule {
    @Binds
    @IntoMap
    @FragmentKey(CategoriesFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideViewCategoriesComponent(ViewCategoriesComponent.Builder builder);
}
