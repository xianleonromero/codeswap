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
    Call<List<ProgrammingLanguage>> getProgrammingLanguages();

    @GET("matches/")
    Call<List<Match>> getMatches();

    @GET("matches/potential/")
    Call<List<Match>> getPotentialMatches();

    @GET("matches/normal/")
    Call<List<Match>> getNormalMatches();

    @POST("matches/refresh/")
    Call<Void> refreshMatches();

    // Nuevos endpoints para sesiones
    @GET("sessions/")
    Call<List<Session>> getSessions();

    @GET("sessions/upcoming/")
    Call<List<Session>> getUpcomingSessions();

    @GET("sessions/past/")
    Call<List<Session>> getPastSessions();

    @POST("sessions/")
    Call<Session> createSession(@Body Map<String, Object> sessionData);

    @PUT("sessions/{id}/")
    Call<Session> updateSessionStatus(@Path("id") int sessionId, @Body Map<String, String> statusUpdate);

    @POST("auth/login/")
    Call<Map<String, Object>> login(@Body Map<String, String> credentials);

    @POST("auth/registration/")
    Call<Map<String, Object>> register(@Body Map<String, String> userData);

    @GET("profile/")
    Call<Map<String, Object>> getProfile();
}