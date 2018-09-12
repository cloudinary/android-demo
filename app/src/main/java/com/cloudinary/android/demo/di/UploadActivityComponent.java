package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.UploadActivity;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 2/05/2018.
 */
@Subcomponent(modules = UploadActivityModule.class)
public interface UploadActivityComponent extends AndroidInjector<UploadActivity> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<UploadActivity> {
    }
}

