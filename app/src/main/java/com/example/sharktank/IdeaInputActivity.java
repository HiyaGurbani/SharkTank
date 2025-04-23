package com.example.sharktank;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class IdeaInputActivity extends AppCompatActivity {

    EditText etIdea;
    Button btnStartPitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_idea_input);

        etIdea = findViewById(R.id.etIdea);
        btnStartPitch = findViewById(R.id.btnStartPitch);

        btnStartPitch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String idea = etIdea.getText().toString().trim();
                if (idea.isEmpty()) {
                    Toast.makeText(IdeaInputActivity.this, "Please enter your idea", Toast.LENGTH_SHORT).show();
                    return;
                }

                Intent intent = new Intent(IdeaInputActivity.this, IdeaPreviewActivity.class);
                intent.putExtra("selected_idea", idea);
                startActivity(intent);
            }
        });
    }
}
