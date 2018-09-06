package com.example.blink22.topgitcontributors;

import android.content.Context;
import android.preference.PreferenceManager;

public class QueryPreferences {

    private static final String PREF_OWNER = "owner";
    private static final String PREF_REPO = "repo";

    private static final String defaultRepo = "ruby";
    private static final String defaultOwner = "ruby";

    public static  String getStoredOwner(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_OWNER,defaultOwner);
    }

    public static String getStoredRepo(Context context){
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString(PREF_REPO, defaultRepo);
    }

    public static void setStoredRepo(Context context, String owner, String repo){
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(PREF_OWNER, owner != null ? owner : defaultOwner)
                .putString(PREF_REPO, repo != null ? repo : defaultRepo).apply();
    }
}
