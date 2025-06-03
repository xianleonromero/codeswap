package com.naix.codeswap.api;

import com.naix.codeswap.models.Match;
import com.naix.codeswap.models.ProgrammingLanguage;
import com.naix.codeswap.models.Session;
import com.naix.codeswap.models.User;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface ApiService {
    @GET("users/")
    Call<List<User>> getUsers();

    @GET("users/{id}/")
    Call<User> getUser(@Path("id") int userId);

    @GET("languages/")
    Call<List<Map<String, Object>>> getProgrammingLanguages();

    @GET("matches/")
    Call<List<Match>> getMatches();

    @GET("matches/potential/")
    Call<List<Map<String, Object>>> getPotentialMatches();

    @GET("matches/normal/")
    Call<List<Map<String, Object>>> getNormalMatches();

    @POST("matches/refresh/")
    Call<Void> refreshMatches();

    // Nuevos endpoints para sesiones
    @GET("sessions/")
    Call<List<Session>> getSessions();

    @GET("sessions/upcoming/")
    Call<List<Map<String, Object>>> getUpcomingSessions();

    @GET("sessions/past/")
    Call<List<Map<String, Object>>> getPastSessions();

    @POST("sessions/")
    Call<Map<String, Object>> createSession(@Body Map<String, Object> sessionData);

    @PUT("sessions/{id}/")
    Call<Map<String, Object>> updateSessionStatus(@Path("id") int sessionId, @Body Map<String, String> statusUpdate);

    @POST("auth/login/")
    Call<Map<String, Object>> login(@Body Map<String, String> credentials);

    @POST("auth/registration/")
    Call<Map<String, Object>> register(@Body Map<String, String> userData);

    @GET("profile/")
    Call<Map<String, Object>> getProfile();

    @PUT("profile/")
    Call<Map<String, Object>> updateProfile(@Body Map<String, Object> profileData);

    @POST("session-requests/")
    Call<Map<String, Object>> requestSession(@Body Map<String, Object> requestData);

    @GET("session-requests/pending/")
    Call<List<Map<String, Object>>> getPendingRequests();

    @POST("session-requests/{id}/respond/")
    Call<Map<String, Object>> respondToRequest(@Path("id") int requestId, @Body Map<String, String> response);

    @GET("notifications/count/")
    Call<Map<String, Object>> getNotificationsCount();
}