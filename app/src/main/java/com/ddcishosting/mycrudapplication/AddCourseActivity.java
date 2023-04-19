package com.ddcishosting.mycrudapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class AddCourseActivity extends AppCompatActivity {
    private EditText editText_courseName, editText_courseID, editText_courseLink, editText_prerequisites, editText_description;
    private Button addCourseButton;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referenceCourse;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_course);

        editText_courseName = findViewById(R.id.edit_course_name);
        editText_courseID = findViewById(R.id.edit_course_id);
        editText_courseLink = findViewById(R.id.edit_course_link);
        editText_prerequisites = findViewById(R.id.edit_course_prerequisites);
        editText_description = findViewById(R.id.edit_course_description);
        progressBar = findViewById(R.id.edit_course_progress_bar);
        firebaseDatabase = FirebaseDatabase.getInstance();
        referenceCourse = firebaseDatabase.getReference("Courses");

        addCourseButton = findViewById(R.id.update_course_button);
        addCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String string_courseName = editText_courseName.getText().toString();
                String string_courseID = editText_courseID.getText().toString();
                String string_courseLink = editText_courseLink.getText().toString();
                String string_prerequisites = editText_prerequisites.getText().toString();
                String string_description = editText_description.getText().toString();

                ReadWriteCourseDetails writeCourseDetails = new ReadWriteCourseDetails(string_courseName, string_courseID, string_courseLink, string_prerequisites, string_description);

                referenceCourse.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.GONE);
                        referenceCourse.child(string_courseID).setValue(writeCourseDetails);
                        Toast.makeText(AddCourseActivity.this, "Course added!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCourseActivity.this, ControlActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddCourseActivity.this, "Error while adding course.\nPlease try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}