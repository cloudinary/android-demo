package com.cloudinary.android.demo.di;

import android.app.Activity;
import android.app.Application;
import android.content.Context;

import com.cloudinary.android.demo.app.CategoriesActivity;
import com.cloudinary.android.demo.app.MainActivity;
import com.cloudinary.android.demo.app.ProductActivity;
import com.cloudinary.android.demo.app.ProductsActivity;
import com.cloudinary.android.demo.app.UploadActivity;
import com.cloudinary.android.demo.data.ProductDao;
import com.cloudinary.android.demo.data.ProductDatabase;
import com.cloudinary.android.demo.data.meta.Prefs;
import com.cloudinary.android.demo.remote.BackendRepository;
import com.cloudinary.android.demo.remote.BackendWebService;
import com.cloudinary.android.demo.remote.CloudinaryRepo;
import com.cloudinary.android.demo.remote.CloudinaryWebService;
import com.cloudinary.android.demo.remote.RemoteProductRepo;
import com.cloudinary.android.demo.util.ConfigurationProvider;
import com.cloudinary.android.demo.util.SystemConfigurationProvider;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import androidx.annotation.NonNull;
import androidx.room.Room;
import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import dagger.android.ActivityKey;
import dagger.android.AndroidInjector;
import dagger.multibindings.IntoMap;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

/**
 * Created by Nitzan Jaitman on 07/02/2018.
 */

@Module(includes = ViewModelModule.class, subcomponents = {MainActivityComponent.class, CategoriesActivityComponent.class, ProductsActivityComponent.class, ProductActivityComponent.class, UploadActivityComponent.class})
public abstract class AppModule {
    @Provides
    @Singleton
    public static Context provideAppContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    public static ConfigurationProvider provideValuesProvider(Context context) {
        return new SystemConfigurationProvider(context);
    }

    @Singleton
    @Provides
    public static ProductDatabase provideProductDatabase(Application application) {
        return Room.databaseBuilder(application.getApplicationContext(),
                ProductDatabase.class, "product.db")
                .build();
    }

    @Singleton
    @Provides
    public static CloudinaryWebService provideCloudinaryWebService() {
        return getRetrofit().create(CloudinaryWebService.class);
    }

    @NonNull
    private static Retrofit getRetrofit() {
        return new Retrofit.Builder()
                .baseUrl("https://res.cloudinary.com/")
                .addConverterFactory(ScalarsConverterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    @Singleton
    @Provides
    @Named("cloudinary")
    public static RemoteProductRepo provideRemoteCloudinaryRepo(Prefs prefs, CloudinaryWebService webService, ConfigurationProvider configurationProvider) {
        return new CloudinaryRepo(webService, prefs, configurationProvider);
    }

    @Singleton
    @Provides
    @Named("backend")
    public static RemoteProductRepo provideBackendRepo(BackendWebService webService, ConfigurationProvider configurationProiver) {
        return new BackendRepository(webService, configurationProiver);
    }

    @Singleton
    @Provides
    public static BackendWebService provideBackendWebService(Context context) {
        return new BackendWebService(context);
    }

    @Singleton
    @Provides
    public static ProductDao provideProductDao(ProductDatabase productDatabase) {
        return productDatabase.getProductDao();
    }

    @Singleton
    @Provides
    public static Prefs providePrefs(Context context) {
        return new Prefs(context);
    }

    @Singleton
    @Provides
    public static Executor provideExecutor() {
        return Executors.newFixedThreadPool(10);
    }

    @Binds
    @IntoMap
    @ActivityKey(MainActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindMainActivityInjectorFactory(MainActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(CategoriesActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindCategoriesActivityInjectorFactory(CategoriesActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(ProductsActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindProductsActivityInjectorFactory(ProductsActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(ProductActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindProductActivityInjectorFactory(ProductActivityComponent.Builder builder);

    @Binds
    @IntoMap
    @ActivityKey(UploadActivity.class)
    abstract AndroidInjector.Factory<? extends Activity> bindUploadActivityInjectorFactory(UploadActivityComponent.Builder builder);
}