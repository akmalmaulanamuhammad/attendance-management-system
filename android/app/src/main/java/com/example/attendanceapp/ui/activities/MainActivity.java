package com.example.attendanceapp.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.attendanceapp.R;
import com.example.attendanceapp.databinding.ActivityAttendanceBinding;
import com.example.attendanceapp.models.AttendanceResponse;
import com.example.attendanceapp.network.ApiClient;
import com.example.attendanceapp.utils.CameraHelper;
import com.example.attendanceapp.utils.LocationHelper;
import com.example.attendanceapp.utils.PermissionHelper;
import com.example.attendanceapp.utils.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;

import java.io.File;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements 
        PermissionHelper.PermissionCallback,
        LocationHelper.LocationUpdateListener {

    private ActivityAttendanceBinding binding;
    private PreferenceManager preferenceManager;
    private CameraHelper cameraHelper;
    private LocationHelper locationHelper;
    private PermissionHelper permissionHelper;
    private Location currentLocation;
    private File photoFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAttendanceBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.toolbar);

        initializeHelpers();
        setupListeners();
        checkPermissions();
    }

    private void initializeHelpers() {
        preferenceManager = new PreferenceManager(this);
        cameraHelper = new CameraHelper(this);
        locationHelper = new LocationHelper(this, this);
        permissionHelper = new PermissionHelper(this, this);
    }

    private void setupListeners() {
        binding.cardPhoto.setOnClickListener(v -> {
            if (permissionHelper.hasPermission(Manifest.permission.CAMERA)) {
                startCamera();
            } else {
                permissionHelper.checkAndRequestPermissions(Manifest.permission.CAMERA);
            }
        });

        binding.btnSubmit.setOnClickListener(v -> submitAttendance());

        binding.fabHistory.setOnClickListener(v -> 
            startActivity(new Intent(this, HistoryActivity.class)));
    }

    private void checkPermissions() {
        String[] permissions = {
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION
        };
        permissionHelper.checkAndRequestPermissions(permissions);
    }

    private void startCamera() {
        try {
            Intent cameraIntent = cameraHelper.getCameraIntent();
            if (cameraIntent != null) {
                startActivityForResult(cameraIntent, Constants.REQUEST_IMAGE_CAPTURE);
            } else {
                showError(getString(R.string.error_camera));
            }
        } catch (Exception e) {
            showError(getString(R.string.error_camera));
        }
    }

    @Override
    public void onLocationUpdate(Location location) {
        currentLocation = location;
        updateLocationUI();
        binding.btnSubmit.setEnabled(photoFile != null);
    }

    @Override
    public void onLocationError(String error) {
        showError(error);
    }

    private void updateLocationUI() {
        if (currentLocation != null) {
            String locationText = getString(R.string.location_format,
                    currentLocation.getLatitude(),
                    currentLocation.getLongitude());
            binding.tvLocation.setText(locationText);
        }
    }

    private void submitAttendance() {
        if (currentLocation == null || photoFile == null) {
            showError(getString(R.string.error_missing_data));
            return;
        }

        showLoading(true);

        // Create multipart request
        RequestBody userIdPart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(preferenceManager.getUserId()));

        RequestBody latitudePart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(currentLocation.getLatitude()));

        RequestBody longitudePart = RequestBody.create(
                MediaType.parse("text/plain"),
                String.valueOf(currentLocation.getLongitude()));

        RequestBody deviceInfoPart = RequestBody.create(
                MediaType.parse("text/plain"),
                android.os.Build.MODEL);

        MultipartBody.Part photoPart = ApiClient.createMultipartBody(photoFile, "photo");

        // Make API call
        ApiClient.getClient().submitAttendance(
                "Bearer " + preferenceManager.getAuthToken(),
                userIdPart,
                latitudePart,
                longitudePart,
                photoPart,
                deviceInfoPart
        ).enqueue(new Callback<AttendanceResponse>() {
            @Override
            public void onResponse(Call<AttendanceResponse> call, Response<AttendanceResponse> response) {
                showLoading(false);
                if (response.isSuccessful() && response.body() != null) {
                    AttendanceResponse attendanceResponse = response.body();
                    if (attendanceResponse.isStatus()) {
                        showSuccess(attendanceResponse.getMessage());
                        resetForm();
                    } else {
                        showError(attendanceResponse.getMessage());
                    }
                } else {
                    showError(getString(R.string.error_server));
                }
            }

            @Override
            public void onFailure(Call<AttendanceResponse> call, Throwable t) {
                showLoading(false);
                showError(getString(R.string.error_network));
            }
        });
    }

    private void resetForm() {
        photoFile = null;
        binding.ivPhoto.setImageResource(R.drawable.placeholder_photo);
        binding.tvPhotoHint.setVisibility(View.VISIBLE);
        binding.btnSubmit.setEnabled(false);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoFile = cameraHelper.processImage();
            if (photoFile != null) {
                binding.ivPhoto.setImageURI(Uri.fromFile(photoFile));
                binding.tvPhotoHint.setVisibility(View.GONE);
                binding.btnSubmit.setEnabled(currentLocation != null);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                         @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        permissionHelper.handlePermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onPermissionGranted(String permission) {
        if (PermissionHelper.isLocationPermission(permission)) {
            locationHelper.startLocationUpdates();
        } else if (PermissionHelper.isCameraPermission(permission)) {
            startCamera();
        }
    }

    @Override
    public void onPermissionDenied(String permission) {
        showError(PermissionHelper.getPermissionRationale(permission));
    }

    @Override
    public void onPermissionPermanentlyDenied(String permission) {
        new AlertDialog.Builder(this)
                .setTitle(R.string.permission_required)
                .setMessage(PermissionHelper.getPermissionRationale(permission))
                .setPositiveButton(R.string.settings, (dialog, which) -> 
                        permissionHelper.openAppSettings())
                .setNegativeButton(R.string.cancel, null)
                .show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_logout) {
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void logout() {
        preferenceManager.clearSession();
        startActivity(new Intent(this, LoginActivity.class)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
        finish();
    }

    private void showLoading(boolean isLoading) {
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSubmit.setEnabled(!isLoading);
        binding.cardPhoto.setEnabled(!isLoading);
    }

    private void showError(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG).show();
    }

    private void showSuccess(String message) {
        Snackbar.make(binding.getRoot(), message, Snackbar.LENGTH_LONG)
                .setBackgroundTint(getColor(R.color.green))
                .setTextColor(getColor(R.color.white))
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationHelper.onDestroy();
        cameraHelper.clearCachedPhotos();
        binding = null;
    }
}
