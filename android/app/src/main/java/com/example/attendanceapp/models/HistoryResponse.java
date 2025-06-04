package com.example.attendanceapp.models;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class HistoryResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private HistoryData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public HistoryData getData() {
        return data;
    }

    public static class HistoryData {
        @SerializedName("records")
        private List<AttendanceRecord> records;

        @SerializedName("pagination")
        private PaginationInfo pagination;

        public List<AttendanceRecord> getRecords() {
            return records;
        }

        public PaginationInfo getPagination() {
            return pagination;
        }
    }

    public static class AttendanceRecord {
        @SerializedName("id")
        private int id;

        @SerializedName("check_in_time")
        private String checkInTime;

        @SerializedName("latitude")
        private double latitude;

        @SerializedName("longitude")
        private double longitude;

        @SerializedName("photo_url")
        private String photoUrl;

        @SerializedName("status")
        private String status;

        @SerializedName("device_info")
        private String deviceInfo;

        @SerializedName("full_name")
        private String fullName;

        public int getId() {
            return id;
        }

        public String getCheckInTime() {
            return checkInTime;
        }

        public double getLatitude() {
            return latitude;
        }

        public double getLongitude() {
            return longitude;
        }

        public String getPhotoUrl() {
            return photoUrl;
        }

        public String getStatus() {
            return status;
        }

        public String getDeviceInfo() {
            return deviceInfo;
        }

        public String getFullName() {
            return fullName;
        }
    }

    public static class PaginationInfo {
        @SerializedName("current_page")
        private int currentPage;

        @SerializedName("total_pages")
        private int totalPages;

        @SerializedName("total_records")
        private int totalRecords;

        @SerializedName("records_per_page")
        private int recordsPerPage;

        public int getCurrentPage() {
            return currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public int getTotalRecords() {
            return totalRecords;
        }

        public int getRecordsPerPage() {
            return recordsPerPage;
        }

        public boolean hasNextPage() {
            return currentPage < totalPages;
        }

        public boolean hasPreviousPage() {
            return currentPage > 1;
        }
    }
}
