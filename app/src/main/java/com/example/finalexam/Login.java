package com.example.finalexam;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;

public class Login extends AppCompatActivity {
private EditText txtEmail, txtPassword;
private Button btnLogin, btnRegister;
private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = FirebaseAuth.getInstance();
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);

        btnLogin.setOnClickListener(v->{
            String email = txtEmail.getText().toString().trim();
            String password = txtPassword.getText().toString().trim();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)){
                Toast.makeText(Login.this, "Please fill the empty fields", Toast.LENGTH_SHORT).show();
                return;
            }

            loginUser(email, password);
        });

        btnRegister.setOnClickListener(v->{
            Intent intent = new Intent(Login.this, Register.class);
            startActivity(intent);
        });
    }

    private void loginUser(String email, String password){
        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if(task.isSuccessful()){
                        Toast.makeText(Login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        navigateToHome();
                    }else {
                        String errorMessage = task.getException() !=null ? task.getException().getMessage() : "Login Failed";
                        Toast.makeText(Login.this, errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void navigateToHome(){
        Intent intent = new Intent(Login.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}