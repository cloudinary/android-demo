package com.cloudinary.android.demo.util;

import android.content.Context;

import java.util.HashMap;
import java.util.Map;

public class Utils {
    /**
     * Build a map with the provided values, index n as key, n+1 as value
     *
     * @param values An even number of strings to pair into a map.
     * @return The constructed map
     */
    public static Map<String, String> asMap(String... values) {
        if (values.length % 2 != 0)
            throw new RuntimeException("Usage - (key, value, key, value, ...)");
        Map<String, String> result = new HashMap<>(values.length / 2);
        for (int i = 0; i < values.length; i += 2) {
            result.put(values[i], values[i + 1]);
        }
        return result;
    }

    public static boolean exists(Context context, String privateFileName) {
        String[] names = context.fileList();
        for (String name : names) {
            if (name.equals(privateFileName)) {
                return true;
            }
        }

        return false;
    }
}
