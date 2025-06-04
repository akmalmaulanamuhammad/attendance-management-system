<?php
require_once '../config/database.php';
require_once '../config/config.php';
require_once '../utils/auth.php';
require_once '../utils/location.php';

// Allow CORS
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: POST");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
    sendResponse(false, 'Only POST method is allowed');
}

// Verify authentication
$auth = new Auth($pdo);
$authResult = $auth->verifyAuth();

if (!$authResult['status']) {
    sendResponse(false, $authResult['message']);
}

// Get user ID from authentication
$userId = $authResult['user_id'];

// Initialize Location class
$location = new Location($pdo);

// Validate form data
if (!isset($_POST['latitude']) || !isset($_POST['longitude'])) {
    sendResponse(false, 'Location coordinates are required');
}

$latitude = $_POST['latitude'];
$longitude = $_POST['longitude'];

// Validate coordinates
$coordValidation = $location->validateCoordinates($latitude, $longitude);
if (!$coordValidation['status']) {
    sendResponse(false, $coordValidation['message']);
}

// Check if location is within office radius
$locationCheck = $location->isWithinOfficeRadius($latitude, $longitude);

// Handle photo upload
if (!isset($_FILES['photo'])) {
    sendResponse(false, 'Photo is required');
}

$photo = $_FILES['photo'];

// Validate photo
if (!in_array($photo['type'], ALLOWED_TYPES)) {
    sendResponse(false, 'Invalid file type. Only JPEG and PNG are allowed');
}

if ($photo['size'] > MAX_FILE_SIZE) {
    sendResponse(false, 'File size exceeds limit of 5MB');
}

// Generate unique filename
$photoName = uniqid() . '_' . time() . '_' . basename($photo['name']);
$photoPath = UPLOAD_PATH . $photoName;

// Create uploads directory if it doesn't exist
if (!file_exists(UPLOAD_PATH)) {
    mkdir(UPLOAD_PATH, 0777, true);
}

// Move uploaded file
if (!move_uploaded_file($photo['tmp_name'], $photoPath)) {
    sendResponse(false, 'Failed to upload photo');
}

// Determine attendance status
$status = $locationCheck['status'] ? 'PRESENT' : 'INVALID_LOCATION';

// Get device info
$deviceInfo = isset($_POST['device_info']) ? $_POST['device_info'] : null;

try {
    // Insert attendance record
    $stmt = $pdo->prepare("
        INSERT INTO attendance_records 
        (user_id, latitude, longitude, photo_path, status, device_info) 
        VALUES (?, ?, ?, ?, ?, ?)
    ");

    $stmt->execute([
        $userId,
        $latitude,
        $longitude,
        $photoName,
        $status,
        $deviceInfo
    ]);

    // Prepare response data
    $responseData = [
        'attendance_id' => $pdo->lastInsertId(),
        'status' => $status,
        'location_check' => $locationCheck,
        'photo_path' => $photoName
    ];

    sendResponse(true, 'Attendance recorded successfully', $responseData);

} catch (PDOException $e) {
    // Delete uploaded file if database insertion fails
    if (file_exists($photoPath)) {
        unlink($photoPath);
    }
    sendResponse(false, 'Database error: ' . $e->getMessage());
}
?>
