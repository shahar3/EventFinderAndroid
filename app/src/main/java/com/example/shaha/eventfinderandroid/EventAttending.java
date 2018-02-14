package com.example.shaha.eventfinderandroid;

import com.example.shaha.eventfinderandroid.Utils.EventType;

import static com.example.shaha.eventfinderandroid.EventsMainActivity.userId;

/**
 * Created by moran on 13/02/2018.
 */

public class EventAttending {
    
    private int ID;
    private String firstName;
    private String lastName;
    private String phoneNumber;

    public EventAttending(int ID, String firstName, String lastName, String phoneNumber) {
        this.setID(ID);
        this.setFirstName(firstName);
        this.setLastName(lastName);
        this.setPhoneNumber(phoneNumber);
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getID() {
        return ID;
    }
    public String getFirstName() {
        return firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public String getFullName() {
        return firstName + " " + lastName;
    }
    public String getPhoneNumber() {
        return phoneNumber;
    }
}
