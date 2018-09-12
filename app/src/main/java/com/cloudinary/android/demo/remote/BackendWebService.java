package com.cloudinary.android.demo.remote;

import android.content.Context;

import com.cloudinary.android.demo.R;
import com.cloudinary.android.demo.data.model.RemoteProduct;
import com.cloudinary.android.demo.util.Utils;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * This class is a place holder for *demo* purposes only - it's treated as a web service
 * by the {@link BackendRepository} but it draws the data from a resource file and persists
 * new user 'uploaded' data to local files. In real world cases this should be replaced by
 * an actual web connector (e.g. an interface implemented by Retrofit generated classes).
 */
@Singleton
public class BackendWebService {
    private static final String LOCAL_FILE_NAME = "user_products.json";
    private final Object localFileLock = new Object();
    private final AtomicInteger largestProductId;
    // NOTE: An actual remote connector won't need the context, we only use it here to
    // access android local resources and files.
    private final Context context;

    @Inject
    public BackendWebService(Context context) {
        this.context = context;
        BackendProductsResult lists = getProductsFromFiles();
        lists.originals.sort(Comparator.comparing(RemoteProduct::getId));
        lists.userUploads.sort(Comparator.comparing(RemoteProduct::getId));
        int largestOriginal = lists.originals.size() == 0 ? 0 : lists.originals.get(lists.originals.size() - 1).getId();
        int largestUserUpload = lists.userUploads.size() == 0 ? 0 : lists.userUploads.get(lists.userUploads.size() - 1).getId();

        largestProductId = new AtomicInteger(Math.max(largestOriginal, largestUserUpload));
    }

    public BackendProductsResult getAllProducts() {
        return getProductsFromFiles();
    }

    public void saveProduct(RemoteProduct product) {
        synchronized (localFileLock) {
            // read all products into list, append product to list, write all products to file
            PrintWriter writer = null;
            try {

                List<RemoteProduct> remotes = new ArrayList<>();
                if (Utils.exists(context, LOCAL_FILE_NAME)) {
                    Collections.addAll(remotes, getRemotesFromFile(context.openFileInput(LOCAL_FILE_NAME)));
                }

                int newId = largestProductId.incrementAndGet();
                product.setId(newId);
                remotes.add(product);
                String json = new Gson().toJson(remotes);
                writer = new PrintWriter(context.openFileOutput(LOCAL_FILE_NAME, Context.MODE_PRIVATE));
                writer.print(json);
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (writer != null) {
                    writer.close();
                }
            }
        }
    }

    private BackendProductsResult getProductsFromFiles() {
        RemoteProduct[] remotes = getRemotesFromFile(context.getResources().openRawResource(R.raw.products_json));
        List<RemoteProduct> originals = new ArrayList<>(Arrays.asList(remotes));
        List<RemoteProduct> fromUser = new ArrayList<>();
        synchronized (localFileLock) {
            RemoteProduct[] remotesFromUser;
            if (Utils.exists(context, LOCAL_FILE_NAME)) {
                try {
                    remotesFromUser = getRemotesFromFile(context.openFileInput(LOCAL_FILE_NAME));
                    fromUser.addAll(Arrays.asList(remotesFromUser));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

        return new BackendProductsResult(originals, fromUser);
    }

    private RemoteProduct[] getRemotesFromFile(InputStream inputStream) {
        try {
            Scanner s = new Scanner(inputStream).useDelimiter("\\A");
            String json = s.hasNext() ? s.next() : "";
            return new Gson().fromJson(json, RemoteProduct[].class);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static final class BackendProductsResult {
        public final List<RemoteProduct> originals;
        public final List<RemoteProduct> userUploads;

        public BackendProductsResult(List<RemoteProduct> originals, List<RemoteProduct> userUpoloads) {
            this.originals = originals;
            this.userUploads = userUpoloads;
        }
    }
}
