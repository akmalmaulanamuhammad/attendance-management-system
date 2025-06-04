<?php
require_once '../config/database.php';
require_once '../config/config.php';
require_once '../utils/auth.php';

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

// Get POST data
$data = json_decode(file_get_contents("php://input"), true);

if (!isset($data['username']) || !isset($data['password'])) {
    sendResponse(false, 'Username and password are required');
}

$username = trim($data['username']);
$password = trim($data['password']);

// Validate input
if (empty($username) || empty($password)) {
    sendResponse(false, 'Username and password cannot be empty');
}

// Initialize Auth class
$auth = new Auth($pdo);

// Attempt login
$result = $auth->login($username, $password);

if ($result['status']) {
    sendResponse(true, 'Login successful', $result['data']);
} else {
    sendResponse(false, $result['message']);
}
?>
