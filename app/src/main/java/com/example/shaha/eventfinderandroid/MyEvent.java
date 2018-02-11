package com.example.shaha.eventfinderandroid;

import android.os.Parcel;
import android.os.Parcelable;

import com.example.shaha.eventfinderandroid.Utils.EventType;

import java.util.ArrayList;
import java.util.Date;

/**
 * Created by shaha on 08/01/2018.
 */

public class MyEvent implements Parcelable{
    private String eventName;
    private String startTime;
    private String endTime;
    private String description;
    private int userId;
    private double longtitude;
    private double latitude;
    private EventType type;

    public MyEvent(String eventName, String startTime, String endTime, String description, int userId, double longtitude, double latitude){
        this.setEventName(eventName);
        this.setStartTime(startTime);
        this.setEndTime(endTime);
        this.setDescription(description);
        this.setUserId(userId);
        this.setLongtitude(longtitude);
        this.setLatitude(latitude);
    }

    public MyEvent(ArrayList<Object> eventFields, String userId){
        this((String)eventFields.get(0),(String)eventFields.get(4),(String)eventFields.get(5),"",(int)eventFields.get(6),(double)eventFields.get(2),(double)eventFields.get(1));
    }

    protected MyEvent(Parcel in) {
        eventName = in.readString();
        description = in.readString();
        userId = in.readInt();
        longtitude = in.readDouble();
        latitude = in.readDouble();
        startTime = in.readString();
        endTime = in.readString();
        type = EventType.values()[in.readInt()]; //get the enum type
    }

    public static final Creator<MyEvent> CREATOR = new Creator<MyEvent>() {
        @Override
        public MyEvent createFromParcel(Parcel in) {
            return new MyEvent(in);
        }

        @Override
        public MyEvent[] newArray(int size) {
            return new MyEvent[size];
        }
    };

    public EventType getType() {
        return type;
    }

    public void setType(EventType type) {
        this.type = type;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public double getLongtitude() {
        return longtitude;
    }

    public void setLongtitude(double longtitude) {
        this.longtitude = longtitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(eventName);
        parcel.writeString(description);
        parcel.writeInt(userId);
        parcel.writeDouble(longtitude);
        parcel.writeDouble(latitude);
        parcel.writeString(startTime);
        parcel.writeString(endTime);
        parcel.writeInt(type.getValue());
    }
}
