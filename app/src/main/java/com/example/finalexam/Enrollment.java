package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Enrollment extends AppCompatActivity {
    private LinearLayout subjectContainer;
    private Button btnEnroll;
    private FirebaseFirestore firestore;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment);

        subjectContainer = findViewById(R.id.subjectContainer);
        btnEnroll = findViewById(R.id.btnEnroll);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        btnEnroll.setOnClickListener(view -> {
            StringBuilder enrolledSubject = new StringBuilder();
            int totalCredits = 0;
            int count = 0;

            // Safely iterate through child views
            for (int i = 0; i < subjectContainer.getChildCount(); i++) {
                if (subjectContainer.getChildAt(i) instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) subjectContainer.getChildAt(i);
                    if (checkBox.isChecked()) {
                        enrolledSubject.append(checkBox.getText()).append("\n");
                        totalCredits += 4;
                        count++;
                    }
                }
            }

            if (count > 6) {
                Toast.makeText(Enrollment.this, "Only a maximum of 6 subjects can be selected", Toast.LENGTH_SHORT).show();
                return;
            }

            if (enrolledSubject.length() > 0) {
                FirebaseUser user = auth.getCurrentUser();
                if (user != null) {
                    String userId = user.getUid();
                    Map<String, Object> subjectData = new HashMap<>();
                    subjectData.put("selectedSubjects", enrolledSubject.toString());
                    subjectData.put("totalCredits", totalCredits);

                    firestore.collection("Subjects")
                            .document(userId)
                            .set(subjectData)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(Enrollment.this, EnrollmentSummary.class);
                                    startActivity(intent);
                                } else {
                                    Toast.makeText(Enrollment.this, "Error saving data", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnFailureListener(e ->
                                    Toast.makeText(Enrollment.this, "Error saving data", Toast.LENGTH_SHORT).show()
                            );
                } else {
                    Toast.makeText(Enrollment.this, "User not logged in", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(Enrollment.this, "No subject selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

}