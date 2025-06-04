package com.example.attendanceapp.network;

import com.example.attendanceapp.models.AttendanceResponse;
import com.example.attendanceapp.models.LoginResponse;
import com.example.attendanceapp.models.HistoryResponse;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;

public interface ApiService {
    @FormUrlEncoded
    @POST(Constants.ENDPOINT_LOGIN)
    Call<LoginResponse> login(
        @Field("username") String username,
        @Field("password") String password
    );

    @Multipart
    @POST(Constants.ENDPOINT_SUBMIT_ATTENDANCE)
    Call<AttendanceResponse> submitAttendance(
        @Header("Authorization") String token,
        @Part("user_id") RequestBody userId,
        @Part("latitude") RequestBody latitude,
        @Part("longitude") RequestBody longitude,
        @Part MultipartBody.Part photo,
        @Part("device_info") RequestBody deviceInfo
    );

    @GET(Constants.ENDPOINT_GET_HISTORY)
    Call<HistoryResponse> getAttendanceHistory(
        @Header("Authorization") String token,
        @Query("page") int page,
        @Query("limit") int limit,
        @Query("start_date") String startDate,
        @Query("end_date") String endDate
    );
}
