package com.example.shaha.eventfinderandroid;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity{
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int RC_PICK_IMAGE = 2;
    private EditText timeEditText;
    private boolean fromBtnClicked;
    private String[] options;
    private Date startTime, endTime;
    private ImageView addEventImg;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Place place;
    private int day, month, year;
    private int dayFinal, monthFinal, yearFinal;
    DatePickerDialog.OnDateSetListener from_dateListener,to_dateListener;
    private TimePickerDialog.OnTimeSetListener from_timeListener,to_timeListener;
    private Date fromDate,toDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Events");

        setupListeners();
    }

    private void setupListeners() {
        //setUpDateBtnListener();
        setUpClockBtnListener();
        setUpSpinner();
        //setUpEventImgListener();
        setUpAddEventBtnListener();
        setUpPickLocationBtnListener();
    }

    private void openDatePicker(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this,listener,year,month,day);
        datePickerDialog.show();
    }

    private void AddEventToDataBase(ArrayList<Object> eventData) {
        /**
         * change null at end of new Mepo event to current user
         */
        DatabaseReference pushReference = mDatabaseReference.push();
        MyEvent newEvent = new MyEvent(eventData,pushReference.getKey());
        pushReference.setValue(newEvent);
        Toast.makeText(AddEventActivity.this, "Event added go have fun", Toast.LENGTH_SHORT).show();
        finish();
    }

    private ArrayList<Object> getEventInfo() {
        ArrayList<Object> eventData = new ArrayList<>();
        //check if all required fields are not empty
        if (isValid()) {
            eventData.add(((EditText) findViewById(R.id.add_event_event_name)).getText().toString());
            eventData.add(place.getLatLng().latitude);
            eventData.add(place.getLatLng().longitude);
            eventData.add(((Spinner) findViewById(R.id.add_event_type_spinner)).getSelectedItem().toString());
            eventData.add(startTime);
            eventData.add(endTime);
            eventData.add(EventsMainActivity.getCurrentUser());
            return eventData;
        } else {
            return null;
        }
    }

    private boolean isValid() {
        if (((EditText) findViewById(R.id.add_event_event_name)).getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Event name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (((EditText) findViewById(R.id.add_event_edit_text_address)).getText().toString().equals("")) {
            Toast.makeText(getApplicationContext(), "Event address is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (startTime == null) {
            Toast.makeText(getApplicationContext(), "Start time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (endTime == null) {
            Toast.makeText(getApplicationContext(), "End time is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setUpSpinner() {
        options = new String[]{"Sport","Trip","Misc"};
        Spinner eventTypeSpinner = (Spinner) findViewById(R.id.add_event_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        eventTypeSpinner.setAdapter(adapter);
    }

    private void setUpPickLocationBtnListener() {
        ImageButton pickPlaceBtn = (ImageButton) findViewById(R.id.add_event_pick_location_btn);
        pickPlaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open the place picker
                openPlacePicker();
            }
        });
    }

    private void openPlacePicker() {
        PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
        try {
            Intent intent = builder.build(AddEventActivity.this);
            startActivityForResult(intent, PLACE_PICKER_REQUEST);
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        }
    }

    private void setUpClockBtnListener() {
        ImageButton timeFromBtn = (ImageButton) findViewById(R.id.add_event_time_from_btn);
        ImageButton timeUntilBtn = (ImageButton) findViewById(R.id.add_event_time_until_btn);
        timeFromBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromBtnClicked = true;
                timeEditText = (EditText) findViewById(R.id.add_event_time_from_edit_text);
                //first pick the date
                openDatePicker(from_dateListener);
                //after pick the time
                openTimePicker(from_timeListener);
            }
        });
        timeUntilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fromBtnClicked = false;
                timeEditText = (EditText) findViewById(R.id.add_event_time_until_edit_text);
                openDatePicker(to_dateListener);
                //after pick the time
                openTimePicker(to_timeListener);
            }
        });
    }

    private void openTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this,listener,hour,minute,true);
    }

    private void setUpAddEventBtnListener() {
        FloatingActionButton addEventFab = (FloatingActionButton) findViewById(R.id.add_event_fab_submit_event);
        addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //AddEventToDataBase(eventData);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != Activity.RESULT_CANCELED) {
            if (requestCode == RC_PICK_IMAGE) {
                Uri selectedImgUri = data.getData();
                Bitmap myImg = BitmapFactory.decodeFile(selectedImgUri.getPath());
                addEventImg.setImageBitmap(myImg);
                addEventImg.setScaleType(ImageView.ScaleType.CENTER_CROP);
            } else if (requestCode == PLACE_PICKER_REQUEST) {
                place = PlacePicker.getPlace(AddEventActivity.this, data);
                updateLocation(place);
            }
        }
    }

    private void updateLocation(Place place) {
        EditText placeAddressEditText = (EditText) findViewById(R.id.add_event_edit_text_address);
        placeAddressEditText.setText(place.getAddress());
    }

    public void setDateAndTimeListener(){
        from_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                yearFinal = i;
                monthFinal = i1;
                dayFinal = i2;

                fromDate = new GregorianCalendar(yearFinal,monthFinal,dayFinal).getTime();
            }
        };

        from_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;

            }
        };

        to_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                yearFinal = i;
                monthFinal = i1;
                dayFinal = i2;

                toDate = new GregorianCalendar(yearFinal,monthFinal,dayFinal).getTime();
            }
        };

        to_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;

                //toDate.setTime();
            }
        };
    }
}
