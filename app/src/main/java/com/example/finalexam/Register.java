package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;
    private Button btnRegister;
    private EditText txtEmail;
    private EditText txtUsername;
    private EditText txtPassword;

    private static final String TAG = "Register";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        Button btnRegister = findViewById(R.id.btnRegister);
        EditText txtEmail = findViewById(R.id.txtEmail);
        EditText txtUsername = findViewById(R.id.txtUsername);
        EditText txtPassword = findViewById(R.id.txtPassword);

        btnRegister.setOnClickListener(view -> {
            String email =txtEmail.getText().toString().trim();
            String username = txtUsername.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(username) || TextUtils.isEmpty(password)){
                Toast.makeText(Register.this, "Please fill out the empty fields", Toast.LENGTH_SHORT).show();
                return;
            }
            if(password.length() < 6){
                Toast.makeText(Register.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }

            registerUser(email, password, username);
        });
    }

    private void registerUser(String email, String password, String username){
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Register.this, "Registration successful", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = auth.getCurrentUser();
                        if(user!=null){
                            createDocument(user, username);
                        }
                    }else {
                        String errorMessage;
                        if (task.getException() instanceof FirebaseAuthException) {
                            errorMessage = ((FirebaseAuthException) task.getException()).getMessage();
                        } else {
                            errorMessage = "Registration failed";
                        }
                        Toast.makeText(Register.this, errorMessage, Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Registration failed: " + errorMessage);
                    }
                });
    }

    private void createDocument(FirebaseUser user, String username){
        String userId = user.getUid();
        Map<String, Object> userData = new HashMap<>();
        userData.put("email", user.getEmail());
        userData.put("username", username);

        firestore.collection("Students")
                .document(userId)
                .set(userData)
                .addOnSuccessListener(aVoid ->{
                    Log.d(TAG, "User document successfully created");
                    Toast.makeText(Register.this, "User document succesfully created", Toast.LENGTH_SHORT).show();
                    navigateToHome();
                }).addOnFailureListener(e ->{
                    Log.e(TAG, "Error creating user document", e);
                    Toast.makeText(Register.this, "Error creating document: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void navigateToHome() {
        Log.d(TAG, "Navigating to Login page");
        Intent intent = new Intent(Register.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
