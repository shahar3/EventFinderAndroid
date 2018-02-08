package com.example.shaha.eventfinderandroid.Utils;

import com.example.shaha.eventfinderandroid.MyEvent;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by shaha on 18/01/2018.
 */

public class MapUtils {
    private static GoogleMap mMap;

    public static void setEventMarkers(GoogleMap map){
        mMap = map;
        ArrayList<MyEvent> events = InternetUtils.getEvents();
        for(MyEvent event:events){
            double latitude = event.getLatitude();
            double longtitude = event.getLongtitude();
            setMarker(latitude,longtitude,event.getEventName());
        }
    }

    private static void setMarker(double latitude, double longtitude,String eventName) {
        mMap.addMarker(new MarkerOptions().position(new LatLng(latitude,longtitude)).title(eventName));
    }
}
