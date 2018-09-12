package com.cloudinary.android.demo.data.meta;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Inject;

/**
 * Android shared preferences wrapper for easier access.
 */
public class Prefs {
    private final SharedPreferences sharedPrefs;

    @Inject
    public Prefs(Context context) {
        this.sharedPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    private SharedPreferences.Editor getEditor() {
        return sharedPrefs.edit();
    }

    public synchronized boolean firstRun() {
        return sharedPrefs.getBoolean("firstRun", true);
    }

    public synchronized void setFirstRun(boolean firstRun) {
        getEditor().putBoolean("firstRun", firstRun).apply();
    }

    public String getUserToken() {
        return sharedPrefs.getString("tag", null);
    }

    public void setUserToken(String tag) {
        getEditor().putString("tag", tag).commit();
    }
}
