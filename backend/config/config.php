<?php
session_start();
error_reporting(E_ALL);
ini_set('display_errors', 1);

// Define constants
define('UPLOAD_PATH', '../uploads/attendance_photos/');
define('ALLOWED_TYPES', ['image/jpeg', 'image/png']);
define('MAX_FILE_SIZE', 5 * 1024 * 1024); // 5MB

// Create uploads directory if it doesn't exist
if (!file_exists(UPLOAD_PATH)) {
    mkdir(UPLOAD_PATH, 0777, true);
}

// Response helper function
function sendResponse($status, $message, $data = null) {
    header('Content-Type: application/json');
    echo json_encode([
        'status' => $status,
        'message' => $message,
        'data' => $data
    ]);
    exit;
}
?>
