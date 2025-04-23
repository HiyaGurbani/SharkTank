package com.example.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {

    private TextView tvResultMessage;
    private Button btnPlayAgain;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tvResultMessage = findViewById(R.id.tvResultMessage);
        btnPlayAgain = findViewById(R.id.btnPlayAgain);

        // Get the result message passed from PitchRoundActivity
        Intent intent = getIntent();
        String resultMessage = intent.getStringExtra("resultMessage");

        // Display the result message
        if (resultMessage != null) {
            tvResultMessage.setText(resultMessage);
        }

        btnPlayAgain.setOnClickListener(v -> {
            // Navigate back to the main activity or another activity
            Intent mainintent = new Intent(ResultActivity.this, MainActivity.class);  // Adjust MainActivity to your main screen class
            startActivity(mainintent);
            finish(); // Optional: to clear this activity from the back stack
        });
    }
}
