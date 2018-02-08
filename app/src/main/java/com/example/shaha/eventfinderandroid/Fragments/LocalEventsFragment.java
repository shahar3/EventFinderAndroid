package com.example.shaha.eventfinderandroid.Fragments;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.shaha.eventfinderandroid.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

public class LocalEventsFragment extends Fragment implements OnMapReadyCallback {
    private static final int RC_GPS_LOCATION = 1;
    private GoogleMap mMap;
    private boolean mapReady = false;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public LocalEventsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_local_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //set the mapFragment to call the mapReady callback when the map is loaded
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //change the camera of the map to point on current location
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        locationListener = new myLocationListener();

        //loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.INTERNET
                },RC_GPS_LOCATION);
                return;
            }
        }else{
            configureLocation();
        }

        Button mapBtn = (Button) view.findViewById(R.id.map_map_btn);
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                }
            }
        });
        Button satelliteBtn = (Button) view.findViewById(R.id.map_satellite_btn);
        satelliteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                }
            }
        });
        Button hybridBtn = (Button) view.findViewById(R.id.map_hybrid_btn);
        hybridBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mapReady) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case RC_GPS_LOCATION:
                if(grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    configureLocation();
                }
                return;
        }
    }

    private void configureLocation() {
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 0, locationListener);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mapReady = true;
        mMap = googleMap;
        //MapUtils.setEventMarkers(mMap);
    }

    private class myLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {
            if(mapReady){
                moveToLocation(loc);
                locationManager.removeUpdates(locationListener);
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {

        }

        @Override
        public void onProviderDisabled(String s) {

        }
    }

    private void moveToLocation(Location loc) {
        LatLng curPosition = new LatLng(loc.getLatitude(),loc.getLongitude());
        CameraPosition target = CameraPosition.builder().target(curPosition).zoom(14).build();
        mMap.moveCamera(CameraUpdateFactory.newCameraPosition(target));
    }
}
