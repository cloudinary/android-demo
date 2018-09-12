package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.UploadPhotoFragment;

import dagger.Subcomponent;
import dagger.android.AndroidInjector;

/**
 * Created by Nitzan Jaitman on 2/05/2018.
 */
@FragmentScope
@Subcomponent(modules = {UploadPhotoModule.class})
public interface UploadPhotoComponent extends AndroidInjector<UploadPhotoFragment> {
    @Subcomponent.Builder
    abstract class Builder extends AndroidInjector.Builder<UploadPhotoFragment> {
    }
}
