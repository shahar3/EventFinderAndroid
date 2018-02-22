package com.example.shaha.eventfinderandroid;

/**
 * Created by shaha on 21/02/2018.
 */

public class EventUser {
    private String email;
    private int id;
    private String phoneNumber;
    private String fullName;

    public EventUser(String email, int id, String phoneNumber, String fullName) {
        this.email = email;
        this.id = id;
        this.phoneNumber = phoneNumber;
        this.fullName = fullName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }
}
