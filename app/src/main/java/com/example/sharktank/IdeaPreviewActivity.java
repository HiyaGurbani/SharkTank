package com.example.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class IdeaPreviewActivity extends AppCompatActivity {

    TextView tvIdeaText;
    Button btnStartPitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_preview);

        tvIdeaText = findViewById(R.id.tvIdeaText);
        btnStartPitch = findViewById(R.id.btnStartPitch);

        String idea = getIntent().getStringExtra("selected_idea");
        tvIdeaText.setText(idea);

        btnStartPitch.setOnClickListener(v -> {
            Intent intent = new Intent(IdeaPreviewActivity.this, PitchRoundActivity.class);
            intent.putExtra("selected_idea", idea);
            startActivity(intent);
        });
    }
}
