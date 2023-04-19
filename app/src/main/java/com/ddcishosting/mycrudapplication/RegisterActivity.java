package com.ddcishosting.mycrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    private EditText editText_firstName, editText_lastName, editText_enrollID, editText_email, editText_password;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        getSupportActionBar().setTitle("Register as a student");
        Toast.makeText(this, "You can register now...", Toast.LENGTH_SHORT).show();

        editText_firstName = findViewById(R.id.register_first_name);
        editText_lastName = findViewById(R.id.register_last_name);
        editText_enrollID = findViewById(R.id.register_enroll_id);
        editText_email = findViewById(R.id.register_email);
        editText_password = findViewById(R.id.register_password);
        progressBar = findViewById(R.id.register_progress_bar);

        Button registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // extracting the entered details
                String string_firstName = editText_firstName.getText().toString();
                String string_lastName = editText_lastName.getText().toString();
                String string_enrollID = editText_enrollID.getText().toString();
                String string_email = editText_email.getText().toString();
                String string_password = editText_password.getText().toString();

                if (TextUtils.isEmpty(string_firstName)) {
                    Toast.makeText(RegisterActivity.this, "First name cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_firstName.setError("First name is required");
                } else if (TextUtils.isEmpty(string_enrollID)) {
                    Toast.makeText(RegisterActivity.this, "Enrollment ID cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_enrollID.setError("Enrollment ID is required");
                } else if (TextUtils.isEmpty(string_email)) {
                    Toast.makeText(RegisterActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_email.setError("Email is required");
                } else if (TextUtils.isEmpty(string_password)) {
                    Toast.makeText(RegisterActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_enrollID.setError("Password is required");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerStudent(string_firstName, string_lastName, string_enrollID, string_email, string_password);
                }
            }
        });

        // open register as admin activity
        TextView textView_registerAdmin = findViewById(R.id.register_as_admin);
        textView_registerAdmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, RegisterAdminActivity.class);
                startActivity(intent);
            }
        });
    }
    private void registerStudent(String string_firstName, String string_lastName, String string_enrollID, String string_email, String string_password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(string_email, string_password).addOnCompleteListener(RegisterActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // updating Display Name
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(string_firstName + " " + string_lastName).build();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(profileChangeRequest);

                        // entering user data into Firebase Realtime Database
                        ReadWriteUserDetails writeUserDetails = new ReadWriteUserDetails(string_enrollID, string_email, string_password);

                        // extracting user reference from database for "Registered Students"
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Students");
                        referenceProfile.child(firebaseUser.getUid()).setValue(writeUserDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterActivity.this, "User registered successfully!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(RegisterActivity.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    } else {
                        throw Objects.requireNonNull(task.getException());
                    }
                } catch (Exception e) {
                    if (e instanceof FirebaseAuthInvalidCredentialsException) {
                        editText_email.setError("Your email is either invalid or already exists. Kindly re-check.");
                        editText_email.requestFocus();
                    } else if (e instanceof FirebaseAuthUserCollisionException) {
                        editText_email.setError("Your email is already in use. Kindly login or use another email.");
                        editText_email.requestFocus();
                    } else {
                        Log.e(TAG, e.getMessage());
                        Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    progressBar.setVisibility(View.GONE);
                    // open MainActivity after registration
                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    // to prevent user from returning back to Register Activity on pressing back button after registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // to close Register Activity
                }
            }
        });
    }

}