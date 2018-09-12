package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductFragment;
import com.cloudinary.android.demo.app.RelatedProductsFragment;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 22/03/2018.
 */

@Module(subcomponents = {ProductComponent.class, RelatedProductsComponent.class})
public abstract class ProductActivityModule {
    @Binds
    @IntoMap
    @FragmentKey(ProductFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideProductComponent(ProductComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(RelatedProductsFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideRelatedProductComponent(RelatedProductsComponent.Builder builder);
}
