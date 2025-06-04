package com.example.attendanceapp.utils;

public class Constants {
    // API Base URL - Change this to your backend URL
    public static final String BASE_URL = "http://your-backend-url.com/api/";

    // API Endpoints
    public static final String ENDPOINT_LOGIN = "login.php";
    public static final String ENDPOINT_SUBMIT_ATTENDANCE = "submit_attendance.php";
    public static final String ENDPOINT_GET_HISTORY = "get_attendance_history.php";

    // Request Codes
    public static final int REQUEST_CAMERA_PERMISSION = 100;
    public static final int REQUEST_LOCATION_PERMISSION = 101;
    public static final int REQUEST_IMAGE_CAPTURE = 102;
    public static final int REQUEST_GALLERY_IMAGE = 103;

    // Location Constants
    public static final long LOCATION_UPDATE_INTERVAL = 10000; // 10 seconds
    public static final long FASTEST_LOCATION_INTERVAL = 5000; // 5 seconds
    public static final float LOCATION_ACCURACY_THRESHOLD = 20.0f; // 20 meters

    // Camera Constants
    public static final String IMAGE_DIRECTORY = "AttendancePhotos";
    public static final int IMAGE_QUALITY = 80; // JPEG quality (0-100)
    public static final int IMAGE_MAX_SIZE = 1024; // Max image dimension

    // Shared Preferences Keys
    public static final String PREF_LAST_LOCATION = "last_location";
    public static final String PREF_LAST_ATTENDANCE = "last_attendance";

    // Bundle Keys
    public static final String KEY_PHOTO_URI = "photo_uri";
    public static final String KEY_LATITUDE = "latitude";
    public static final String KEY_LONGITUDE = "longitude";
    public static final String KEY_TIMESTAMP = "timestamp";

    // Error Messages
    public static final String ERROR_CAMERA_UNAVAILABLE = "Camera is not available on this device";
    public static final String ERROR_LOCATION_DISABLED = "Location services are disabled";
    public static final String ERROR_NETWORK = "Network error occurred";
    public static final String ERROR_SERVER = "Server error occurred";
    public static final String ERROR_INVALID_CREDENTIALS = "Invalid username or password";
    public static final String ERROR_LOCATION_PERMISSION = "Location permission is required";
    public static final String ERROR_CAMERA_PERMISSION = "Camera permission is required";

    // Success Messages
    public static final String SUCCESS_ATTENDANCE = "Attendance recorded successfully";
    public static final String SUCCESS_LOGIN = "Login successful";

    // Dialog Titles
    public static final String DIALOG_TITLE_ERROR = "Error";
    public static final String DIALOG_TITLE_SUCCESS = "Success";
    public static final String DIALOG_TITLE_PERMISSION = "Permission Required";
    public static final String DIALOG_TITLE_LOADING = "Please wait...";

    // Validation Constants
    public static final int MIN_PASSWORD_LENGTH = 6;
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-zA-Z]).{6,}$";
    public static final String EMAIL_PATTERN = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    // Time Constants
    public static final long SPLASH_DELAY = 2000; // 2 seconds
    public static final int TOKEN_REFRESH_INTERVAL = 45 * 60 * 1000; // 45 minutes

    private Constants() {
        // Private constructor to prevent instantiation
    }
}
