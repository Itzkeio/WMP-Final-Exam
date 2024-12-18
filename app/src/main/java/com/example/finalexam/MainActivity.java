package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class MainActivity extends AppCompatActivity {
    private Button btnSelect, btnSummary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        btnSelect = findViewById(R.id.btnSelect);
        btnSummary = findViewById(R.id.btnSummary);

        btnSelect.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, Enrollment.class);
            startActivity(intent);
            finish();
        });

        btnSummary.setOnClickListener(v->{
            Intent intent = new Intent(MainActivity.this, EnrollmentSummary.class);
            startActivity(intent);
            finish();
        });
    }
}