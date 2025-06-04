package com.example.attendanceapp.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Looper;

import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.Priority;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

public class LocationHelper {
    private static final int REQUEST_CHECK_SETTINGS = 1001;
    
    private final Context context;
    private final FusedLocationProviderClient fusedLocationClient;
    private final LocationCallback locationCallback;
    private final LocationRequest locationRequest;
    private LocationUpdateListener listener;

    public interface LocationUpdateListener {
        void onLocationUpdate(Location location);
        void onLocationError(String error);
    }

    public LocationHelper(Context context, LocationUpdateListener listener) {
        this.context = context;
        this.listener = listener;
        
        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);

        // Create location request
        locationRequest = new LocationRequest.Builder(Priority.PRIORITY_HIGH_ACCURACY)
                .setIntervalMillis(Constants.LOCATION_UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(Constants.FASTEST_LOCATION_INTERVAL)
                .build();

        // Initialize location callback
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if (location != null && location.getAccuracy() <= Constants.LOCATION_ACCURACY_THRESHOLD) {
                        listener.onLocationUpdate(location);
                        stopLocationUpdates(); // Stop updates after getting accurate location
                        return;
                    }
                }
            }
        };
    }

    public void startLocationUpdates() {
        if (!hasLocationPermission()) {
            listener.onLocationError("Location permission not granted");
            return;
        }

        // Check if location settings are satisfied
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        SettingsClient settingsClient = LocationServices.getSettingsClient(context);
        Task<LocationSettingsResponse> task = settingsClient.checkLocationSettings(builder.build());

        task.addOnSuccessListener(locationSettingsResponse -> {
            // Location settings are satisfied, start location updates
            if (ActivityCompat.checkSelfPermission(context, 
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                fusedLocationClient.requestLocationUpdates(locationRequest,
                        locationCallback,
                        Looper.getMainLooper());
            }
        });

        task.addOnFailureListener(e -> {
            if (e instanceof ResolvableApiException) {
                try {
                    // Show dialog to enable location settings
                    ResolvableApiException resolvable = (ResolvableApiException) e;
                    resolvable.startResolutionForResult((Activity) context,
                            REQUEST_CHECK_SETTINGS);
                } catch (IntentSender.SendIntentException sendEx) {
                    listener.onLocationError("Could not show location settings dialog");
                }
            } else {
                listener.onLocationError("Location settings are not satisfied");
            }
        });
    }

    public void stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback);
    }

    public void getLastKnownLocation() {
        if (!hasLocationPermission()) {
            listener.onLocationError("Location permission not granted");
            return;
        }

        if (ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            listener.onLocationUpdate(location);
                        } else {
                            startLocationUpdates();
                        }
                    })
                    .addOnFailureListener(e -> 
                            listener.onLocationError("Failed to get last known location"));
        }
    }

    private boolean hasLocationPermission() {
        return ActivityCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    public void onDestroy() {
        stopLocationUpdates();
        listener = null;
    }
}
