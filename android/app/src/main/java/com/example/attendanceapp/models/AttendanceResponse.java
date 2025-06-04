package com.example.attendanceapp.models;

import com.google.gson.annotations.SerializedName;

public class AttendanceResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private AttendanceData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public AttendanceData getData() {
        return data;
    }

    public static class AttendanceData {
        @SerializedName("attendance_id")
        private int attendanceId;

        @SerializedName("status")
        private String status;

        @SerializedName("location_check")
        private LocationCheck locationCheck;

        @SerializedName("photo_path")
        private String photoPath;

        public int getAttendanceId() {
            return attendanceId;
        }

        public String getStatus() {
            return status;
        }

        public LocationCheck getLocationCheck() {
            return locationCheck;
        }

        public String getPhotoPath() {
            return photoPath;
        }
    }

    public static class LocationCheck {
        @SerializedName("status")
        private boolean status;

        @SerializedName("office")
        private String office;

        @SerializedName("distance")
        private int distance;

        @SerializedName("message")
        private String message;

        public boolean isStatus() {
            return status;
        }

        public String getOffice() {
            return office;
        }

        public int getDistance() {
            return distance;
        }

        public String getMessage() {
            return message;
        }
    }
}
