package com.example.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.sharktank.BuildConfig;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PitchRoundActivity extends AppCompatActivity {

    private String selectedIdea;
    private int round = 0;
    private int score = 0;

    private TextView tvSelectedIdea, tvRoundStatus;
    private Button btnPitch1, btnPitch2, btnPitch3, btnNextRound;

    private List<List<String>> allPitchRounds = new ArrayList<>();
    private List<String> correctPitches = new ArrayList<>();
    private String currentCorrectPitch = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pitch_round);

        tvSelectedIdea = findViewById(R.id.tvSelectedIdea);
        tvRoundStatus = findViewById(R.id.tvRoundStatus);
        btnPitch1 = findViewById(R.id.btnPitch1);
        btnPitch2 = findViewById(R.id.btnPitch2);
        btnPitch3 = findViewById(R.id.btnPitch3);
        btnNextRound = findViewById(R.id.btnNextRound);

        selectedIdea = getIntent().getStringExtra("selected_idea");
        tvSelectedIdea.setText(selectedIdea);

        preloadPitchRounds(selectedIdea);

        btnPitch1.setOnClickListener(v -> checkAnswer(btnPitch1.getText().toString()));
        btnPitch2.setOnClickListener(v -> checkAnswer(btnPitch2.getText().toString()));
        btnPitch3.setOnClickListener(v -> checkAnswer(btnPitch3.getText().toString()));

        btnNextRound.setOnClickListener(v -> {
            round++;
            if (round < 3) {
                showPitchRound(round);
                btnNextRound.setVisibility(View.GONE);
            } else {
                showResult();
            }
        });
    }

    private void preloadPitchRounds(String idea) {
        String prompt = "Give me 3 rounds of pitch options for a startup idea: \"" + idea + "\". " +
                "For each round, provide 3 one-line pitch options. Number them 1, 2, 3, with the first option being the strongest investment-worthy pitch. " +
                "Just give the lines for round 1, round 2, and round 3.";

        GeminiRequest.Part part = new GeminiRequest.Part(prompt);
        GeminiRequest.Content content = new GeminiRequest.Content(Collections.singletonList(part));
        GeminiRequest request = new GeminiRequest(Collections.singletonList(content));

        String BASE_URL = "https://generativelanguage.googleapis.com/";
        String GEMINI_API_KEY = BuildConfig.GEMINI_API_KEY;

        GeminiApiService apiService = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(GeminiApiService.class);

        Call<GeminiResponse> call = apiService.generateContent(GEMINI_API_KEY, request);

        call.enqueue(new Callback<GeminiResponse>() {
            @Override
            public void onResponse(Call<GeminiResponse> call, Response<GeminiResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        String rawText = response.body().candidates.get(0).content.parts.get(0).text;
                        String[] lines = rawText.split("\n");

                        List<String> currentRound = new ArrayList<>();

                        for (String line : lines) {
                            String cleaned = line.replaceFirst("^\\d+\\.\\s*", "").trim();
                            if (!cleaned.isEmpty()) {
                                currentRound.add(cleaned);
                                if (currentRound.size() == 3) {
                                    // First is always correct
                                    correctPitches.add(currentRound.get(0));
                                    allPitchRounds.add(new ArrayList<>(currentRound));
                                    currentRound.clear();
                                }
                            }
                        }

                        if (allPitchRounds.size() >= 3) {
                            showPitchRound(0); // start the first round
                        } else {
                            Toast.makeText(PitchRoundActivity.this, "Not enough pitch rounds received", Toast.LENGTH_SHORT).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(PitchRoundActivity.this, "Parse error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(PitchRoundActivity.this, "Failed to generate pitch options", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GeminiResponse> call, Throwable t) {
                Toast.makeText(PitchRoundActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void showPitchRound(int index) {
        List<String> roundPitches = new ArrayList<>(allPitchRounds.get(index));
        Collections.shuffle(roundPitches);
        currentCorrectPitch = correctPitches.get(index);

        tvRoundStatus.setText("Round " + (index + 1) + " of 3");

        btnPitch1.setText(roundPitches.get(0));
        btnPitch2.setText(roundPitches.get(1));
        btnPitch3.setText(roundPitches.get(2));
    }

    private void checkAnswer(String selectedPitch) {
        if (selectedPitch.equals(currentCorrectPitch)) {
            score++;
        }

        if (round < 2) {
            btnNextRound.setVisibility(View.VISIBLE);
        } else {
            showResult();
        }
    }

    private void showResult() {
        String resultMessage;
        if (score == 3) {
            resultMessage = "Investor fully on board!";
        } else if (score == 2) {
            resultMessage = "Investor intrigued but needs more!";
        } else {
            resultMessage = "Investor is not interested.";
        }

        // Pass the result message to ResultActivity
        Intent rintent = new Intent(PitchRoundActivity.this, ResultActivity.class);
        rintent.putExtra("resultMessage", resultMessage);
        startActivity(rintent);

        // Finish the current activity so that the user can't go back to the PitchRoundActivity
        finish();
    }
}
