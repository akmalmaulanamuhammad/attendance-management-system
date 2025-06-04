<?php
require_once '../config/database.php';
require_once '../config/config.php';
require_once '../utils/auth.php';

// Allow CORS
header("Access-Control-Allow-Origin: *");
header("Access-Control-Allow-Methods: GET");
header("Access-Control-Allow-Headers: Content-Type, Authorization");
header("Content-Type: application/json; charset=UTF-8");

// Handle preflight requests
if ($_SERVER['REQUEST_METHOD'] == 'OPTIONS') {
    http_response_code(200);
    exit;
}

if ($_SERVER['REQUEST_METHOD'] !== 'GET') {
    sendResponse(false, 'Only GET method is allowed');
}

// Verify authentication
$auth = new Auth($pdo);
$authResult = $auth->verifyAuth();

if (!$authResult['status']) {
    sendResponse(false, $authResult['message']);
}

// Get user ID from authentication
$userId = $authResult['user_id'];

// Get query parameters
$page = isset($_GET['page']) ? (int)$_GET['page'] : 1;
$limit = isset($_GET['limit']) ? (int)$_GET['limit'] : 10;
$startDate = isset($_GET['start_date']) ? $_GET['start_date'] : null;
$endDate = isset($_GET['end_date']) ? $_GET['end_date'] : null;

// Validate pagination parameters
if ($page < 1) $page = 1;
if ($limit < 1) $limit = 10;
if ($limit > 100) $limit = 100;

$offset = ($page - 1) * $limit;

try {
    // Build query
    $query = "
        SELECT 
            ar.id,
            ar.check_in_time,
            ar.latitude,
            ar.longitude,
            ar.photo_path,
            ar.status,
            ar.device_info,
            u.full_name
        FROM attendance_records ar
        JOIN users u ON ar.user_id = u.id
        WHERE ar.user_id = ?
    ";
    $params = [$userId];

    // Add date filters if provided
    if ($startDate) {
        $query .= " AND ar.check_in_time >= ?";
        $params[] = $startDate . ' 00:00:00';
    }
    if ($endDate) {
        $query .= " AND ar.check_in_time <= ?";
        $params[] = $endDate . ' 23:59:59';
    }

    // Add ordering
    $query .= " ORDER BY ar.check_in_time DESC";

    // Get total count
    $countStmt = $pdo->prepare(str_replace('SELECT *', 'SELECT COUNT(*)', $query));
    $countStmt->execute($params);
    $totalRecords = $countStmt->fetchColumn();

    // Add pagination
    $query .= " LIMIT ? OFFSET ?";
    $params[] = $limit;
    $params[] = $offset;

    // Execute main query
    $stmt = $pdo->prepare($query);
    $stmt->execute($params);
    $records = $stmt->fetchAll();

    // Process records
    $records = array_map(function($record) {
        // Add full photo URL
        $record['photo_url'] = 'uploads/attendance_photos/' . $record['photo_path'];
        
        // Format date
        $record['check_in_time'] = date('Y-m-d H:i:s', strtotime($record['check_in_time']));
        
        return $record;
    }, $records);

    // Prepare pagination info
    $totalPages = ceil($totalRecords / $limit);

    $responseData = [
        'records' => $records,
        'pagination' => [
            'current_page' => $page,
            'total_pages' => $totalPages,
            'total_records' => $totalRecords,
            'records_per_page' => $limit
        ]
    ];

    sendResponse(true, 'Attendance history retrieved successfully', $responseData);

} catch (PDOException $e) {
    sendResponse(false, 'Database error: ' . $e->getMessage());
}
?>
