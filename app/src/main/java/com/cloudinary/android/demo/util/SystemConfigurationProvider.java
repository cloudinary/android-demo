package com.cloudinary.android.demo.util;

import android.content.Context;

import com.cloudinary.android.demo.R;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is intended to abstract android logic from other modules that need config values.
 */
@Singleton
public class SystemConfigurationProvider implements ConfigurationProvider {
    private final Context context;

    @Inject
    public SystemConfigurationProvider(Context context) {
        this.context = context;
    }

    @Override
    public String[] getDepartments() {
        return context.getResources().getStringArray(R.array.gender_choices);
    }

    @Override
    public String getGlobalTag() {
        return context.getString(R.string.global_tag);
    }

    @Override
    public String getCloudName() {
        return context.getResources().getString(R.string.main_cloud_name);
    }

    @Override
    public String getSharedCloudName() {
        return context.getResources().getString(R.string.shared_cloud_name);
    }
}
