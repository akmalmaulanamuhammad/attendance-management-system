<?php
require_once __DIR__ . '/../config/database.php';

class Auth {
    private $pdo;
    private static $key = "your_jwt_secret_key"; // Change this in production

    public function __construct($pdo) {
        $this->pdo = $pdo;
    }

    public function login($username, $password) {
        try {
            $stmt = $this->pdo->prepare("SELECT id, password, full_name FROM users WHERE username = ?");
            $stmt->execute([$username]);
            $user = $stmt->fetch();

            if ($user && password_verify($password, $user['password'])) {
                return [
                    'status' => true,
                    'data' => [
                        'user_id' => $user['id'],
                        'full_name' => $user['full_name'],
                        'token' => $this->generateToken($user['id'])
                    ]
                ];
            }
            return ['status' => false, 'message' => 'Invalid credentials'];
        } catch (PDOException $e) {
            return ['status' => false, 'message' => 'Database error: ' . $e->getMessage()];
        }
    }

    private function generateToken($userId) {
        $header = json_encode(['typ' => 'JWT', 'alg' => 'HS256']);
        $payload = json_encode([
            'user_id' => $userId,
            'iat' => time(),
            'exp' => time() + (60 * 60) // Token expires in 1 hour
        ]);

        $base64UrlHeader = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($header));
        $base64UrlPayload = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($payload));

        $signature = hash_hmac('sha256', $base64UrlHeader . "." . $base64UrlPayload, self::$key, true);
        $base64UrlSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($signature));

        return $base64UrlHeader . "." . $base64UrlPayload . "." . $base64UrlSignature;
    }

    public function validateToken($token) {
        try {
            list($header, $payload, $signature) = explode(".", $token);
            
            $decodedPayload = json_decode(base64_decode(str_replace(['-', '_'], ['+', '/'], $payload)), true);
            
            if ($decodedPayload['exp'] < time()) {
                return ['status' => false, 'message' => 'Token has expired'];
            }

            $validSignature = hash_hmac('sha256', $header . "." . $payload, self::$key, true);
            $base64UrlValidSignature = str_replace(['+', '/', '='], ['-', '_', ''], base64_encode($validSignature));

            if ($base64UrlValidSignature === $signature) {
                return ['status' => true, 'user_id' => $decodedPayload['user_id']];
            }

            return ['status' => false, 'message' => 'Invalid token'];
        } catch (Exception $e) {
            return ['status' => false, 'message' => 'Token validation failed'];
        }
    }

    public function verifyAuth() {
        $headers = getallheaders();
        if (!isset($headers['Authorization'])) {
            return ['status' => false, 'message' => 'No authorization token provided'];
        }

        $token = str_replace('Bearer ', '', $headers['Authorization']);
        return $this->validateToken($token);
    }
}
?>
