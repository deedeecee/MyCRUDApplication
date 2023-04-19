package com.ddcishosting.mycrudapplication;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class ReadWriteCourseDetails implements Parcelable {
    private String courseName, courseID, courseLink, prerequisites, description;

    public ReadWriteCourseDetails() {}

    public ReadWriteCourseDetails(String courseName, String courseID, String courseLink, String prerequisites, String description) {
        this.courseName = courseName;
        this.courseID = courseID;
        this.courseLink = courseLink;
        this.prerequisites = prerequisites;
        this.description = description;
    }

    protected ReadWriteCourseDetails(Parcel in) {
        courseName = in.readString();
        courseID = in.readString();
        courseLink = in.readString();
        prerequisites = in.readString();
        description = in.readString();
    }

    public static final Creator<ReadWriteCourseDetails> CREATOR = new Creator<ReadWriteCourseDetails>() {
        @Override
        public ReadWriteCourseDetails createFromParcel(Parcel in) {
            return new ReadWriteCourseDetails(in);
        }

        @Override
        public ReadWriteCourseDetails[] newArray(int size) {
            return new ReadWriteCourseDetails[size];
        }
    };

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseID() {
        return courseID;
    }

    public void setCourseID(String courseID) {
        this.courseID = courseID;
    }

    public String getCourseLink() {
        return courseLink;
    }

    public void setCourseLink(String courseLink) {
        this.courseLink = courseLink;
    }

    public String getPrerequisites() {
        return prerequisites;
    }

    public void setPrerequisites(String prerequisites) {
        this.prerequisites = prerequisites;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(courseName);
        dest.writeString(courseID);
        dest.writeString(courseLink);
        dest.writeString(prerequisites);
        dest.writeString(description);
    }
}
