package com.cloudinary.android.demo.remote;

import com.cloudinary.android.demo.data.model.CloudinaryJsonResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Nitzan Jaitman on 11/02/2018.
 */

/**
 * Access cloudinary's list-resources-by-tag endpoint. This interface is implemented by
 * retrofit's code generation
 */
public interface CloudinaryWebService {
    @GET("/{cloud}/image/list/v{version}/{tag}.json")
    Call<CloudinaryJsonResponse> getProducts(@Path("cloud") String cloudName, @Path("tag") String tag, @Path("version") long version);
}
