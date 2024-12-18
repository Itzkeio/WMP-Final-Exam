package com.example.finalexam;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class EnrollmentSummary extends AppCompatActivity {
    private TextView subjectsList, totalCredits;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_enrollment_summary);
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), (v, insets) -> {
            v.setPadding(
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).left,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).top,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).right,
                    insets.getInsets(WindowInsetsCompat.Type.systemBars()).bottom
            );
            return insets;
        });

        // Initialize UI elements
        subjectsList = findViewById(R.id.subjectsList);
        totalCredits = findViewById(R.id.totalCredits);
        firestore = FirebaseFirestore.getInstance();
        auth = FirebaseAuth.getInstance();

        // Fetch Firebase user
        FirebaseUser user = auth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();

            // Fetch subject data from Firestore
            firestore.collection("Subjects").document(userId).get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot documentSnapshot = task.getResult();
                            if (documentSnapshot.exists()) {
                                // Safely retrieve data with null checks
                                String selectedSubjects = documentSnapshot.getString("selectedSubjects");
                                Long totalCreditsLong = documentSnapshot.getLong("totalCredits");
                                int total = totalCreditsLong != null ? totalCreditsLong.intValue() : 0;

                                subjectsList.setText(selectedSubjects != null ? selectedSubjects : "No subjects selected");
                                totalCredits.setText("Total credits: " + total);
                            } else {
                                // Handle case where document doesn't exist
                                subjectsList.setText("No subjects selected");
                                totalCredits.setText("Total credits: 0");
                            }
                        } else {
                            // Handle Firestore retrieval failure
                            subjectsList.setText("Error loading subjects");
                            totalCredits.setText("Total credits: 0");
                            Toast.makeText(EnrollmentSummary.this, "Error retrieving data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            // Handle null Firebase user
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show();
            subjectsList.setText("No subjects selected");
            totalCredits.setText("Total credits: 0");
        }
    }
}
