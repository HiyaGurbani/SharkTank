package com.example.sharktank;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Query;

public interface GeminiApiService {
//    @Headers({
//            "Content-Type: application/json"
//    })
    @POST("v1beta/models/gemini-2.0-flash:generateContent")
    Call<GeminiResponse> generateContent(
//            @Url String url,  // Use @Url for the full URL string
            @Query("key") String apiKey,
            @Body GeminiRequest request  // Accept the body for the request
    );
}
