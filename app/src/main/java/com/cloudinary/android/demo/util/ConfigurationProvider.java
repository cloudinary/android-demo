package com.cloudinary.android.demo.util;

/**
 * An implementation of this interface is used by modules that need access to configuration values.
 */
public interface ConfigurationProvider {

    String[] getDepartments();

    String getGlobalTag();

    String getCloudName();

    String getSharedCloudName();
}
