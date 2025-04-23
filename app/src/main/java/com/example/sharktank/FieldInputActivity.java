package com.example.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.sharktank.*;

import androidx.appcompat.app.AppCompatActivity;


import java.util.Collections;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.*;
import retrofit2.converter.gson.GsonConverterFactory;

public class FieldInputActivity extends AppCompatActivity {

    EditText etField;
    Button btnGenerateIdeas;
    LinearLayout layoutIdeas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_field_input);

        etField = findViewById(R.id.etField);
        btnGenerateIdeas = findViewById(R.id.btnGenerateIdeas);
        layoutIdeas = findViewById(R.id.layoutIdeas);

        btnGenerateIdeas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String field = etField.getText().toString().trim();
                if (field.isEmpty()) {
                    Toast.makeText(FieldInputActivity.this, "Please enter a field", Toast.LENGTH_SHORT).show();
                    return;
                }

                generateStartupIdeas(field);
            }
        });
    }

    private void generateStartupIdeas(String field) {
        layoutIdeas.removeAllViews();

        String BASE_URL = "https://generativelanguage.googleapis.com/";
        String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY; // Replace with your real key

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        com.example.sharktank.GeminiApiService apiService = retrofit.create(com.example.sharktank.GeminiApiService.class);

        String prompt = "Give 3 startup ideas in the field of " + field +
                ". Each should be a single sentence (under 25 words), numbered 1 to 3. No explanation or extra text.";

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        GeminiRequest request = new GeminiRequest(Collections.singletonList(content));

        Call<GeminiResponse> call = apiService.generateContent(
                GEMINI_API_KEY,
                request
        );

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String text = response.body().candidates.get(0).content.parts.get(0).text;
                    showIdeas(text);
                } else {
                    Toast.makeText(FieldInputActivity.this, "Failed to get ideas", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Toast.makeText(FieldInputActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showIdeas(String rawText) {
        layoutIdeas.removeAllViews();
        String[] ideas = rawText.split("\n");

        for (String rawIdea : ideas) {
            String idea = rawIdea
                    .replaceAll("^\\d+\\.\\s*", "")   // remove leading "1. "
                    .replaceAll("\\*\\*", "")         // remove markdown bold
                    .trim();
            if (!idea.isEmpty()) {
                Button ideaButton = new Button(this);
                ideaButton.setBackgroundResource(R.drawable.custom_button_bg);
                ideaButton.setText(idea);
                ideaButton.setAllCaps(false);
                ideaButton.setPadding(20, 10, 20, 10);
                ideaButton.setTextSize(16);

                // Now it's fine to use 'idea' in the lambda
                ideaButton.setOnClickListener(v -> {
                    Intent intent = new Intent(FieldInputActivity.this, IdeaPreviewActivity.class);
                    intent.putExtra("selected_idea", idea);
                    startActivity(intent);
                });

                layoutIdeas.addView(ideaButton);
            }
        }
    }
}
