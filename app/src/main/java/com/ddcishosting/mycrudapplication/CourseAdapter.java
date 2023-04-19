package com.ddcishosting.mycrudapplication;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class CourseAdapter extends RecyclerView.Adapter<CourseAdapter.ViewHolder> {
    private ArrayList<ReadWriteCourseDetails> readCourseDetails;
    private Context context;
    int lastPos = -1;
    private CourseClickInterface courseClickInterface;

    public CourseAdapter(ArrayList<ReadWriteCourseDetails> readCourseDetails, Context context, CourseClickInterface courseClickInterface) {
        this.readCourseDetails = readCourseDetails;
        this.context = context;
        this.courseClickInterface = courseClickInterface;
    }

    @NonNull
    @Override
    public CourseAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.course_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CourseAdapter.ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        ReadWriteCourseDetails courseDetails = readCourseDetails.get(position);
        holder.textView_courseName.setText(courseDetails.getCourseName());
        holder.textView_courseID.setText(courseDetails.getCourseID());

        setAnimation(holder.itemView, position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                courseClickInterface.onCourseClick(position);
            }
        });
    }

    private void setAnimation(View itemView, int position) {
        if (position > lastPos) {
            Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left);
            itemView.setAnimation(animation);
            lastPos = position;
        }
    }

    @Override
    public int getItemCount() {
        return readCourseDetails.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView textView_courseName, textView_courseID;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_courseName = itemView.findViewById(R.id.textView_item_course_name);
            textView_courseID = itemView.findViewById(R.id.textView_item_course_id);
            textView_courseID = itemView.findViewById(R.id.textView_item_course_id);
        }
    }

    public interface CourseClickInterface {
        void onCourseClick(int position);
    }
}
