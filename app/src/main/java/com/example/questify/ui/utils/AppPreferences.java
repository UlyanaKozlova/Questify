package com.example.questify.ui.utils;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.appcompat.app.AppCompatDelegate;

public class AppPreferences {

    private static final String PREFS_NAME = "questify_prefs";
    private static final String KEY_LANGUAGE = "language";
    private static final String KEY_NIGHT_MODE = "night_mode";

    public static void saveLanguage(Context ctx, String lang) {
        getPrefs(ctx).edit().putString(KEY_LANGUAGE, lang).apply();
    }

    public static String getLanguage(Context ctx) {
        return getPrefs(ctx).getString(KEY_LANGUAGE, "");
    }

    public static void saveNightMode(Context ctx, int mode) {
        getPrefs(ctx).edit().putInt(KEY_NIGHT_MODE, mode).apply();
    }

    public static int getNightMode(Context ctx) {
        return getPrefs(ctx).getInt(KEY_NIGHT_MODE, AppCompatDelegate.MODE_NIGHT_YES);
    }

    private static SharedPreferences getPrefs(Context ctx) {
        return ctx.getApplicationContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
    }
}
