package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductsGridFragment;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 14/03/2018.
 */
@Module(subcomponents = {ViewProductsComponent.class})
public abstract class ProductsActivityModule {
    @Binds
    @IntoMap
    @FragmentKey(ProductsGridFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideViewProductsComponent(ViewProductsComponent.Builder builder);
}
