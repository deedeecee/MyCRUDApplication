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

import java.util.HashMap;
import java.util.Map;

public class EditCourseActivity extends AppCompatActivity {
    private EditText editText_courseName, editText_courseLink, editText_prerequisites, editText_description;
    private String courseID;
    private Button updateCourseButton, deleteCourseButton;
    private ProgressBar progressBar;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference referenceCourse;
    private ReadWriteCourseDetails editCourseDetails;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_course);

        firebaseDatabase = FirebaseDatabase.getInstance();
        editText_courseName = findViewById(R.id.edit_course_name);
        editText_courseLink = findViewById(R.id.edit_course_link);
        editText_prerequisites = findViewById(R.id.edit_course_prerequisites);
        editText_description = findViewById(R.id.edit_course_description);

        progressBar = findViewById(R.id.edit_course_progress_bar);

        updateCourseButton = findViewById(R.id.update_course_button);
        deleteCourseButton = findViewById(R.id.delete_course_button);

        // getting previous data of course
        editCourseDetails = getIntent().getParcelableExtra("course");
        if (editCourseDetails != null) {
            editText_courseName.setText(editCourseDetails.getCourseName());
            editText_courseLink.setText(editCourseDetails.getCourseLink());
            editText_prerequisites.setText(editCourseDetails.getPrerequisites());
            editText_description.setText(editCourseDetails.getDescription());
            courseID = editCourseDetails.getCourseID();
        }

        referenceCourse = firebaseDatabase.getReference("Courses").child(courseID);
        updateCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBar.setVisibility(View.VISIBLE);
                String string_courseName = editText_courseName.getText().toString();
                String string_courseLink = editText_courseLink.getText().toString();
                String string_prerequisites = editText_prerequisites.getText().toString();
                String string_description = editText_description.getText().toString();

                Map<String, Object> map = new HashMap<>();
                map.put("courseName", string_courseName);
                map.put("courseLink", string_courseLink);
                map.put("prerequisites", string_prerequisites);
                map.put("description", string_description);
                map.put("courseID", courseID);

                referenceCourse.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        progressBar.setVisibility(View.GONE);
                        referenceCourse.updateChildren(map);

                        Toast.makeText(EditCourseActivity.this, "Course updated!", Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(EditCourseActivity.this, ControlActivity.class);
                        startActivity(intent);
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(EditCourseActivity.this, "Failed to update course.\nPlease try again!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
        deleteCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteCourse();
            }
        });
    }

    private void deleteCourse() {
        referenceCourse.removeValue();
        Toast.makeText(this, "Course deleted!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(EditCourseActivity.this, ControlActivity.class);
        startActivity(intent);
        finish();
    }
}