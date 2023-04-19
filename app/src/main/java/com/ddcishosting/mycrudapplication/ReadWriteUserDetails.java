package com.ddcishosting.mycrudapplication;

public class ReadWriteUserDetails {
    public String enrollID, email, password;

    public ReadWriteUserDetails() {}

    public ReadWriteUserDetails(String enrollID, String email, String password) {
        this.enrollID = enrollID;
        this.email = email;
        this.password = password;
    }

    public String getEnrollID() {
        return enrollID;
    }

    public void setEnrollID(String enrollID) {
        this.enrollID = enrollID;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
