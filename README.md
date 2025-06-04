# Attendance Management System

A comprehensive attendance management system with location-based verification and photo capture functionality.

## Features

- User authentication with JWT
- Location-based attendance verification
- Photo capture for attendance proof
- Attendance history with filtering
- Real-time location validation
- Secure file handling
- Responsive UI design

## Backend (PHP)

The backend is built with PHP and MySQL, providing RESTful APIs for:
- User authentication
- Attendance submission
- Attendance history retrieval
- Location verification

### API Endpoints

- `POST /api/login.php` - User authentication
- `POST /api/submit_attendance.php` - Submit attendance with photo and location
- `GET /api/get_attendance_history.php` - Retrieve attendance history

## Android App

The Android app is built with Java and follows MVVM architecture pattern.

### Key Components

- Material Design UI components
- CameraX for photo capture
- Google Play Services Location
- Retrofit for network calls
- Glide for image loading
- ViewBinding for view handling

### Features

1. **Authentication**
   - Secure login with token-based authentication
   - Session management
   - Auto-login capability

2. **Attendance Marking**
   - Real-time location tracking
   - Camera integration for photo capture
   - Location validation against office coordinates
   - Automatic device info collection

3. **History View**
   - Paginated attendance history
   - Date range filtering
   - Pull-to-refresh functionality
   - Detailed attendance records

## Setup

### Backend Setup

1. Configure database in `backend/config/database.php`
2. Import schema from `backend/database/schema.sql`
3. Update base URL in `backend/config/config.php`
4. Configure allowed office locations in `backend/utils/location.php`

### Android Setup

1. Update `BASE_URL` in `Constants.java`
2. Configure build.gradle with required dependencies
3. Update AndroidManifest.xml with required permissions

## Security Features

- JWT token authentication
- Password hashing
- File upload validation
- Location verification
- CORS protection
- SQL injection prevention

## Requirements

### Backend
- PHP 7.4+
- MySQL 5.7+
- Apache/Nginx web server

### Android
- Android SDK 21+
- Google Play Services
- Camera capability
- Location services

## License

This project is licensed under the MIT License - see the LICENSE file for details.
