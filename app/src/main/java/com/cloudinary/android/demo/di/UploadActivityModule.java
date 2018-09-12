package com.cloudinary.android.demo.di;

import com.cloudinary.android.demo.app.UploadDetailsFragment;
import com.cloudinary.android.demo.app.UploadPhotoFragment;

import androidx.fragment.app.Fragment;
import dagger.Binds;
import dagger.Module;
import dagger.android.AndroidInjector;
import dagger.android.support.FragmentKey;
import dagger.multibindings.IntoMap;

/**
 * Created by Nitzan Jaitman on 2/05/2018.
 */

@Module(subcomponents = {UploadPhotoComponent.class, UploadDetailsComponent.class})
public abstract class UploadActivityModule {
    @Binds
    @IntoMap
    @FragmentKey(UploadPhotoFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideUploadPhotoComponent(UploadPhotoComponent.Builder builder);

    @Binds
    @IntoMap
    @FragmentKey(UploadDetailsFragment.class)
    abstract AndroidInjector.Factory<? extends Fragment> provideUploadDetailsComponent(UploadDetailsComponent.Builder builder);
}
