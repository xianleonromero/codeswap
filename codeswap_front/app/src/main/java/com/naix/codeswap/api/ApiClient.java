package com.naix.codeswap.api;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    private static final String BASE_URL = "https://codeswap-68w3.onrender.com/api/";
    private static Retrofit retrofit = null;
    private static Context context;

    public static void init(Context appContext) {
        context = appContext.getApplicationContext();
    }

    public static Retrofit getClient() {
        if (retrofit == null) {
            // Crear OkHttpClient con interceptor para token
            OkHttpClient.Builder httpClient = new OkHttpClient.Builder();

            // Añadir interceptor de autenticación
            httpClient.addInterceptor(new Interceptor() {
                @Override
                public Response intercept(Chain chain) throws IOException {
                    Request original = chain.request();

                    // Obtener token si existe
                    String token = null;
                    if (context != null) {
                        SharedPreferences prefs = context.getSharedPreferences("CodeSwapPrefs", Context.MODE_PRIVATE);
                        token = prefs.getString("auth_token", null);
                    }

                    // Si hay token, añadirlo al header
                    if (token != null) {
                        Request.Builder requestBuilder = original.newBuilder()
                                .header("Authorization", "Token " + token)
                                .method(original.method(), original.body());

                        Request request = requestBuilder.build();
                        return chain.proceed(request);
                    }

                    return chain.proceed(original);
                }
            });

            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(httpClient.build())
                    .build();
        }
        return retrofit;
    }
}