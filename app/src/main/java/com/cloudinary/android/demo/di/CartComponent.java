package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.CartFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 13/03/2018.
 */
@FragmentScope
@Subcomponent(modules = CartModule.class)
public interface CartComponent extends AndroidInjector<CartFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<CartFragment> {
    }
}
