package com.example.attendanceapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionHelper {
    private final Context context;
    private final Activity activity;
    private final PermissionCallback callback;

    public interface PermissionCallback {
        void onPermissionGranted(String permission);
        void onPermissionDenied(String permission);
        void onPermissionPermanentlyDenied(String permission);
    }

    public PermissionHelper(Activity activity, PermissionCallback callback) {
        this.context = activity.getApplicationContext();
        this.activity = activity;
        this.callback = callback;
    }

    public boolean checkAndRequestPermissions(String... permissions) {
        List<String> permissionsNeeded = new ArrayList<>();

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(context, permission)
                    != PackageManager.PERMISSION_GRANTED) {
                permissionsNeeded.add(permission);
            }
        }

        if (!permissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(activity,
                    permissionsNeeded.toArray(new String[0]),
                    getRequestCode(permissionsNeeded.get(0)));
            return false;
        }

        return true;
    }

    public void handlePermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        String permission = getPermissionFromRequestCode(requestCode);
        
        if (permission != null && grantResults.length > 0) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                callback.onPermissionGranted(permission);
            } else {
                if (!ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
                    callback.onPermissionPermanentlyDenied(permission);
                } else {
                    callback.onPermissionDenied(permission);
                }
            }
        }
    }

    private int getRequestCode(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return Constants.REQUEST_CAMERA_PERMISSION;
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return Constants.REQUEST_LOCATION_PERMISSION;
            default:
                return 0;
        }
    }

    private String getPermissionFromRequestCode(int requestCode) {
        switch (requestCode) {
            case Constants.REQUEST_CAMERA_PERMISSION:
                return Manifest.permission.CAMERA;
            case Constants.REQUEST_LOCATION_PERMISSION:
                return Manifest.permission.ACCESS_FINE_LOCATION;
            default:
                return null;
        }
    }

    public boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(context, permission)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
        intent.setData(uri);
        activity.startActivity(intent);
    }

    public static String getPermissionRationale(String permission) {
        switch (permission) {
            case Manifest.permission.CAMERA:
                return "Camera permission is required to capture attendance photos.";
            case Manifest.permission.ACCESS_FINE_LOCATION:
                return "Location permission is required to verify your attendance location.";
            default:
                return "This permission is required for the app to function properly.";
        }
    }

    public static boolean isLocationPermission(String permission) {
        return permission.equals(Manifest.permission.ACCESS_FINE_LOCATION) ||
               permission.equals(Manifest.permission.ACCESS_COARSE_LOCATION);
    }

    public static boolean isCameraPermission(String permission) {
        return permission.equals(Manifest.permission.CAMERA);
    }
}
