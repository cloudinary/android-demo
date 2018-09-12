package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.ProductsGridFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

@FragmentScope
@Subcomponent(modules = ViewProductsModule.class)
public interface ViewProductsComponent extends AndroidInjector<ProductsGridFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<ProductsGridFragment> {
    }
}
