package com.example.shaha.eventfinderandroid.Utils;

import com.example.shaha.eventfinderandroid.MyEvent;
import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by shaha on 11/02/2018.
 */

public class EventItem implements ClusterItem {
    private final LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private MyEvent event;

    public EventItem(double lat, double lng, MyEvent event) {
        mPosition = new LatLng(lat, lng);
        this.event = event;
    }

    public EventItem(double lat, double lng, String title, String snippet, MyEvent event) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        mSnippet = snippet;
        this.event = event;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }

    public MyEvent getEvent() {
        return event;
    }
}
