package com.naix.codeswap.api;

import com.naix.codeswap.models.Match;
import com.naix.codeswap.models.ProgrammingLanguage;
import com.naix.codeswap.models.User;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.POST;
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
}