package com.ddcishosting.mycrudapplication;

public class ReadWriteAdminDetails {
    public String email, password;

    public ReadWriteAdminDetails() {}

    public ReadWriteAdminDetails(String email, String password) {
        this.email = email;
        this.password = password;
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
