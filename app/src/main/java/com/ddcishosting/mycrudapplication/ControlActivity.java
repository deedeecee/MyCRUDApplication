package com.ddcishosting.mycrudapplication;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Objects;

public class ControlActivity extends AppCompatActivity implements CourseAdapter.CourseClickInterface{
    private RecyclerView recyclerView_courses;
    private ProgressBar progressBar;
    private FloatingActionButton floatingAction_addButton;
    private FirebaseAuth auth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private ArrayList<ReadWriteCourseDetails> courseDetails;
    private RelativeLayout relativeLayout_bottomSheet;
    private CourseAdapter courseAdapter;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_control);
        Objects.requireNonNull(getSupportActionBar()).setTitle("Control Panel");

        recyclerView_courses = findViewById(R.id.control_courses);
        progressBar = findViewById(R.id.control_progress_bar);
        floatingAction_addButton = findViewById(R.id.control_add_fab);
        auth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("Courses");
        courseDetails = new ArrayList<>();
        relativeLayout_bottomSheet = findViewById(R.id.relative_layout_bottom_sheet);
        courseAdapter = new CourseAdapter(courseDetails, this, this);
        recyclerView_courses.setLayoutManager(new LinearLayoutManager(this));
        recyclerView_courses.setAdapter(courseAdapter);
        floatingAction_addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlActivity.this, AddCourseActivity.class);
                startActivity(intent);
            }
        });
        
        getAllCourses();
    }

    private void getAllCourses() {
        courseDetails.clear();
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                courseDetails.add(snapshot.getValue(ReadWriteCourseDetails.class));
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                progressBar.setVisibility(View.GONE);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                progressBar.setVisibility(View.GONE);
                courseAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // no need to do anything
            }
        });
    }

    @Override
    public void onCourseClick(int position) {
        displayBottomSheet(courseDetails.get(position));
    }

    private void displayBottomSheet(ReadWriteCourseDetails readWriteCourseDetails) {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layout = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_dialog, relativeLayout_bottomSheet);
        bottomSheetDialog.setContentView(layout);
        bottomSheetDialog.setCancelable(false);
        bottomSheetDialog.setCanceledOnTouchOutside(true);
        bottomSheetDialog.show();

        TextView textView_courseName = layout.findViewById(R.id.bottom_sheet_course_name);
        TextView textView_description = layout.findViewById(R.id.bottom_sheet_description);
        TextView textView_prerequisites = layout.findViewById(R.id.bottom_sheet_prerequisites);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button editCourseButton = layout.findViewById(R.id.bottom_sheet_edit_course_button);
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) Button viewDetailsButton = layout.findViewById(R.id.bottom_sheet_course_details_button);

        textView_courseName.setText(readWriteCourseDetails.getCourseName());
        textView_description.setText(readWriteCourseDetails.getDescription());
        textView_prerequisites.setText(readWriteCourseDetails.getPrerequisites());

        editCourseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ControlActivity.this, EditCourseActivity.class);
                intent.putExtra("course", readWriteCourseDetails);
                startActivity(intent);
            }
        });

        viewDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(readWriteCourseDetails.getCourseLink()));
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.menu_logout:
                Toast.makeText(this, "User logged out...", Toast.LENGTH_SHORT).show();
                auth.signOut();
                Intent intent = new Intent(ControlActivity.this, LoginActivity.class);
                startActivity(intent);
                this.finish();

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}