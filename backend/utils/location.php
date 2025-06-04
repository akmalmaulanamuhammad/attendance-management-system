<?php
require_once __DIR__ . '/../config/database.php';

class Location {
    private $pdo;

    public function __construct($pdo) {
        $this->pdo = $pdo;
    }

    // Calculate distance between two points using Haversine formula
    public function calculateDistance($lat1, $lon1, $lat2, $lon2) {
        $earthRadius = 6371000; // Earth's radius in meters

        $latFrom = deg2rad($lat1);
        $lonFrom = deg2rad($lon1);
        $latTo = deg2rad($lat2);
        $lonTo = deg2rad($lon2);

        $latDelta = $latTo - $latFrom;
        $lonDelta = $lonTo - $lonFrom;

        $angle = 2 * asin(sqrt(pow(sin($latDelta / 2), 2) +
            cos($latFrom) * cos($latTo) * pow(sin($lonDelta / 2), 2)));

        return $angle * $earthRadius;
    }

    // Check if user is within office radius
    public function isWithinOfficeRadius($userLat, $userLon) {
        try {
            $stmt = $this->pdo->prepare("SELECT * FROM office_locations");
            $stmt->execute();
            $offices = $stmt->fetchAll();

            foreach ($offices as $office) {
                $distance = $this->calculateDistance(
                    $userLat,
                    $userLon,
                    $office['latitude'],
                    $office['longitude']
                );

                if ($distance <= $office['radius_meters']) {
                    return [
                        'status' => true,
                        'office' => $office['location_name'],
                        'distance' => round($distance)
                    ];
                }
            }

            return [
                'status' => false,
                'message' => 'Location is outside of all office radius zones',
                'nearest_distance' => round($distance)
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'message' => 'Database error: ' . $e->getMessage()
            ];
        }
    }

    // Validate coordinates
    public function validateCoordinates($lat, $lon) {
        // Check if coordinates are numeric
        if (!is_numeric($lat) || !is_numeric($lon)) {
            return [
                'status' => false,
                'message' => 'Invalid coordinates format'
            ];
        }

        // Check latitude range (-90 to 90)
        if ($lat < -90 || $lat > 90) {
            return [
                'status' => false,
                'message' => 'Invalid latitude value'
            ];
        }

        // Check longitude range (-180 to 180)
        if ($lon < -180 || $lon > 180) {
            return [
                'status' => false,
                'message' => 'Invalid longitude value'
            ];
        }

        return ['status' => true];
    }

    // Get nearest office location
    public function getNearestOffice($userLat, $userLon) {
        try {
            $stmt = $this->pdo->prepare("SELECT * FROM office_locations");
            $stmt->execute();
            $offices = $stmt->fetchAll();

            $nearestOffice = null;
            $shortestDistance = PHP_FLOAT_MAX;

            foreach ($offices as $office) {
                $distance = $this->calculateDistance(
                    $userLat,
                    $userLon,
                    $office['latitude'],
                    $office['longitude']
                );

                if ($distance < $shortestDistance) {
                    $shortestDistance = $distance;
                    $nearestOffice = [
                        'office' => $office,
                        'distance' => round($distance)
                    ];
                }
            }

            return [
                'status' => true,
                'data' => $nearestOffice
            ];
        } catch (PDOException $e) {
            return [
                'status' => false,
                'message' => 'Database error: ' . $e->getMessage()
            ];
        }
    }
}
?>
