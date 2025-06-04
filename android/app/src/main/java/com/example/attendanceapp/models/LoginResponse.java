package com.example.attendanceapp.models;

import com.google.gson.annotations.SerializedName;

public class LoginResponse {
    @SerializedName("status")
    private boolean status;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private UserData data;

    public boolean isStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }

    public UserData getData() {
        return data;
    }

    public static class UserData {
        @SerializedName("user_id")
        private int userId;

        @SerializedName("full_name")
        private String fullName;

        @SerializedName("token")
        private String token;

        public int getUserId() {
            return userId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getToken() {
            return token;
        }
    }
}
