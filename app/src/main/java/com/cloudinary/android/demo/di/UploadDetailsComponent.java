package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.UploadDetailsFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 2/05/2018.
 */
@FragmentScope
@Subcomponent(modules = {UploadDetailsModule.class})
public interface UploadDetailsComponent extends AndroidInjector<UploadDetailsFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<UploadDetailsFragment> {
    }
}