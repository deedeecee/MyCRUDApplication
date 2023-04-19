package com.ddcishosting.mycrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    private EditText editText_username, editText_password;
    private ProgressBar progressBar;
    private FirebaseAuth authProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Login Activity");

        editText_username = findViewById(R.id.login_username);
        editText_password = findViewById(R.id.login_password);
        progressBar = findViewById(R.id.login_progress_bar);

        authProfile = FirebaseAuth.getInstance();

        Button button_login = findViewById(R.id.main_act_login_button);
        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String string_username = editText_username.getText().toString();
                String string_password = editText_password.getText().toString();

                if (TextUtils.isEmpty(string_username) || TextUtils.isEmpty(string_password)) {
                    Toast.makeText(LoginActivity.this, "All fields are mandatory", Toast.LENGTH_LONG).show();
                    editText_username.requestFocus();
                    editText_password.requestFocus();
                } else {
                    loginUser(string_username, string_password);
                }

                progressBar.setVisibility(View.VISIBLE);
            }
        });

        // open register activity
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView registerText = findViewById(R.id.registerClick);
        registerText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
    }

    private void loginUser(String string_username, String string_password) {
        authProfile.signInWithEmailAndPassword(string_username + "@tezu.ac.in", string_password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null && user.getEmail().startsWith("csb")) {
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (user != null && user.getEmail().startsWith("adm")) {
                        Intent intent = new Intent(LoginActivity.this, ControlActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Authentication failed", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}