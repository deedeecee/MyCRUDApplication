package com.ddcishosting.mycrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import java.util.Objects;

public class RegisterAdminActivity extends AppCompatActivity {
    private EditText editText_adminID, editText_email, editText_password, editText_adminCode;
    private ProgressBar progressBar;
    private static final String TAG = "RegisterAdminActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_admin);

        getSupportActionBar().setTitle("Register as an admin");
        Toast.makeText(this, "You can register now...", Toast.LENGTH_SHORT).show();

        editText_adminID = findViewById(R.id.reg_admin_id);
        editText_email = findViewById(R.id.reg_admin_email);
        editText_password = findViewById(R.id.reg_admin_password);
        editText_adminCode = findViewById(R.id.admin_secret_code);
        progressBar = findViewById(R.id.register_admin_progress_bar);

        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button registerButton = findViewById(R.id.reg_admin_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // obtaining the entered details into String variables
                String string_adminID = editText_adminID.getText().toString();
                String string_email = editText_email.getText().toString();
                String string_password = editText_password.getText().toString();
                String string_code = editText_adminCode.getText().toString();

                if (!isValidEmail(string_email)) {
                    Toast.makeText(RegisterAdminActivity.this, "Please enter a valid email address!", Toast.LENGTH_SHORT).show();
                    editText_email.setError("Invalid email address");
                    return;
                }

                if (TextUtils.isEmpty(string_adminID)) {
                    Toast.makeText(RegisterAdminActivity.this, "Admin ID cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_adminID.setError("Admin ID is required");
                } else if (TextUtils.isEmpty(string_email)) {
                    Toast.makeText(RegisterAdminActivity.this, "Email cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_email.setError("Email is required");
                } else if (TextUtils.isEmpty(string_password)) {
                    Toast.makeText(RegisterAdminActivity.this, "Password cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_email.setError("Password is required");
                } else if (TextUtils.isEmpty(string_code)) {
                    Toast.makeText(RegisterAdminActivity.this, "Admin code cannot be empty!", Toast.LENGTH_SHORT).show();
                    editText_adminCode.setError("Admin code is required");
                } else if (!TextUtils.equals(string_code, "anka2001")) {
                    Toast.makeText(RegisterAdminActivity.this, "Please re-check the admin code!", Toast.LENGTH_SHORT).show();
                    editText_adminCode.setError("Admin code is incorrect");
                } else {
                    progressBar.setVisibility(View.VISIBLE);
                    registerAdmin(string_adminID, string_email, string_password);
                }
            }
        });
    }

    private void registerAdmin(String string_adminID, String string_email, String string_password) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.createUserWithEmailAndPassword(string_email, string_password).addOnCompleteListener(RegisterAdminActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                try {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = auth.getCurrentUser();

                        // updating Display Name to be AdminID
                        UserProfileChangeRequest profileChangeRequest = new UserProfileChangeRequest.Builder().setDisplayName(string_adminID).build();
                        assert firebaseUser != null;
                        firebaseUser.updateProfile(profileChangeRequest);

                        // entering admin data into Firebase Realtime Database
                        ReadWriteAdminDetails writeAdminDetails = new ReadWriteAdminDetails(string_email, string_password);

                        // extracting admin reference from database for 'Registered Admins'
                        DatabaseReference referenceProfile = FirebaseDatabase.getInstance().getReference("Registered Admins");
                        referenceProfile.child(firebaseUser.getUid()).setValue(writeAdminDetails).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Toast.makeText(RegisterAdminActivity.this, "Admin registered successfully.", Toast.LENGTH_LONG).show();

//                                  // open admin control profile after successful registration
                                    Intent intent = new Intent(RegisterAdminActivity.this, ControlActivity.class);
                                    // to prevent admin from returning back to Register Admin Activity on pressing back button after registration
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    finish(); // to close Register Admin Activity
                                } else {
                                    Toast.makeText(RegisterAdminActivity.this, "User registered failed. Please try again.", Toast.LENGTH_LONG).show();
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
                        Toast.makeText(RegisterAdminActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                } finally {
                    progressBar.setVisibility(View.GONE);
                    // open MainActivity after registration
                    Intent intent = new Intent(RegisterAdminActivity.this, ControlActivity.class);
                    // to prevent user from returning back to Register Activity on pressing back button after registration
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish(); // to close Register Activity
                }
            }
        });
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
