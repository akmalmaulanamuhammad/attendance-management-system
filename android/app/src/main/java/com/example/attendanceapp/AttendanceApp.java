package com.example.attendanceapp;

import android.app.Application;
import android.content.Context;

import com.example.attendanceapp.utils.PreferenceManager;

public class AttendanceApp extends Application {
    private static Context context;
    private static PreferenceManager preferenceManager;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        preferenceManager = new PreferenceManager(context);
    }

    public static Context getAppContext() {
        return context;
    }

    public static PreferenceManager getPreferenceManager() {
        return preferenceManager;
    }
}
