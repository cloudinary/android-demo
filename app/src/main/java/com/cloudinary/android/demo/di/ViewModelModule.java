package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.viewmodel.CartViewModel;
import com.cloudinary.android.demo.viewmodel.ProductViewModel;
import com.cloudinary.android.demo.viewmodel.ProductsViewModel;
import com.cloudinary.android.demo.viewmodel.UploadViewModel;
import com.cloudinary.android.demo.viewmodel.ViewModelFactory;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import dagger.Binds;
import dagger.Module;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

@Module
public abstract class ViewModelModule {
    @Binds
    @IntoMap
    @ViewModelKey(ProductsViewModel.class)
    abstract ViewModel bindProductsViewModel(ProductsViewModel productsViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(CartViewModel.class)
    abstract ViewModel bindCartViewModel(CartViewModel cartViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(ProductViewModel.class)
    abstract ViewModel bindProductViewModel(ProductViewModel productViewModel);

    @Binds
    @IntoMap
    @ViewModelKey(UploadViewModel.class)
    abstract ViewModel bindUploadViewModel(UploadViewModel uploadViewModel);

    @Binds
    abstract ViewModelProvider.Factory bindViewModelFactory(ViewModelFactory factory);
}
