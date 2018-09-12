package com.cloudinary.android.demo.app;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.meta.Prefs;
import com.cloudinary.android.demo.di.DaggerAppComponent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.inject.Inject;

import dagger.android.AndroidInjector;
import dagger.android.DaggerApplication;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

public class MainApplication extends DaggerApplication {
    @Inject
    Prefs prefs;

    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize the Cloudinary Media manager in your app's main entry point, in this case
        // only the cloud name needs to be set up:
        Map<String, Object> config = new HashMap<>();
        config.put("cloud_name", getString(R.string.shared_cloud_name));
        config.put("secure", true);
        MediaManager.init(this, config);

        if (prefs.firstRun()) {
            // Here we simulate a 'user' token to pass along when communicating with the backend.
            // In a real app this should be the actual credentials, stored somewhere safe.
            String uniqueID = UUID.randomUUID().toString();
            prefs.setFirstRun(false);
            prefs.setUserToken(uniqueID);
        }
    }

    @Override
    protected AndroidInjector<? extends DaggerApplication> applicationInjector() {
        return DaggerAppComponent.builder().application(this).build();
    }
}
