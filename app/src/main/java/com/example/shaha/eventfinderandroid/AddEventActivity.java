package com.example.shaha.eventfinderandroid;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.shaha.eventfinderandroid.Utils.ImageManager;
import com.example.shaha.eventfinderandroid.Utils.InternetUtils;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.CloudBlockBlob;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class AddEventActivity extends AppCompatActivity {
    private static final int PLACE_PICKER_REQUEST = 1;
    private static final int RC_PICK_IMAGE = 2;
    private static final int SELECT_IMAGE = 3;
    private EditText timeFromEditText, timeUntilEditText;
    private String[] options;
    private ImageView addEventImg;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;
    private Place place;
    private int day, month, year;
    private int dayFinal, monthFinal, yearFinal;
    DatePickerDialog.OnDateSetListener from_dateListener, to_dateListener;
    private TimePickerDialog.OnTimeSetListener from_timeListener, to_timeListener;
    private Date fromDate, toDate;
    private Uri selectedImage;
    private String eventName;
    private String eventAddress;
    private String startTime;
    private String endTime;
    private double longtitude;
    private double latitude;
    private int type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference().child("Events");

        timeFromEditText = (EditText) findViewById(R.id.add_event_time_from_edit_text);
        timeUntilEditText = (EditText) findViewById(R.id.add_event_time_until_edit_text);

        setupListeners();
    }

    private void setupListeners() {
        setUpClockBtnListener();
        setUpSpinner();
        setDateAndTimeListener();
        setUpEventImgListener();
        setUpAddEventBtnListener();
        setUpPickLocationBtnListener();
    }

    private void setUpEventImgListener() {
        addEventImg = (ImageView) findViewById(R.id.add_event_img);
        //on click open the gallery and choose a photo from there
        addEventImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //TODO: open gallery and choose a picture
                openGallery();
            }
        });
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);//
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_IMAGE);
    }

    private void openDatePicker(DatePickerDialog.OnDateSetListener listener) {
        Calendar calendar = Calendar.getInstance();
        day = calendar.get(Calendar.DAY_OF_MONTH);
        month = calendar.get(Calendar.MONTH);
        year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AddEventActivity.this, listener, year, month, day);
        datePickerDialog.show();
    }

    /**
     * Check if all the required fields aren't empty
     *
     * @return
     */
    private boolean isValid() {
        if (eventName.equals("")) {
            Toast.makeText(getApplicationContext(), "Event name is required", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (eventAddress.equals("")) {
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
        options = new String[]{"Sport", "Carpool", "Sale", "Party", "Bar sale", "Misc"};
        Spinner eventTypeSpinner = (Spinner) findViewById(R.id.add_event_type_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, options);
        eventTypeSpinner.setAdapter(adapter);
        eventTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                type = position + 1;
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = 1;
            }
        });
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
                //first pick the date
                openDatePicker(from_dateListener);
            }
        });
        timeUntilBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDatePicker(to_dateListener);
            }
        });
    }

    private void openTimePicker(TimePickerDialog.OnTimeSetListener listener) {
        Calendar now = Calendar.getInstance();
        int hour = now.get(Calendar.HOUR_OF_DAY);
        int minute = now.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(AddEventActivity.this, listener, hour, minute, true);
        timePickerDialog.show();
    }

    private void setUpAddEventBtnListener() {
        FloatingActionButton addEventFab = (FloatingActionButton) findViewById(R.id.add_event_fab_submit_event);
        addEventFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //upload to database
                AddEventToDataBase();
            }
        });
    }

    private void AddEventToDataBase() {
        //pre process - get all the events form fields
        extractFields();
        //1. validate event
        if (isValid()) {
            //upload to database in a different thread
            AddEventAsyncTask task = new AddEventAsyncTask();
            task.execute();
        }
    }

    private void extractFields() {
        eventName = ((EditText) findViewById(R.id.add_event_event_name)).getText().toString();
        eventAddress = ((EditText) findViewById(R.id.add_event_edit_text_address)).getText().toString();
        startTime = timeFromEditText.getText().toString();
        endTime = timeUntilEditText.getText().toString();
    }

    private void UploadImage(int eventID) {
        UploadImageAsyncTask task = new UploadImageAsyncTask(eventID);
        task.execute(selectedImage);
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
            } else if (requestCode == SELECT_IMAGE) {
                if (data != null) {
                    selectedImage = data.getData();
                    addEventImg.setImageURI(selectedImage);
                }
            }
        }
    }

    private class AddEventAsyncTask extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int ans = InternetUtils.createEvent("description", eventName, startTime, endTime, latitude, longtitude, EventsMainActivity.getCurrentUser(), type);
            return ans;
        }

        @Override
        protected void onPostExecute(Integer eventID) {
            super.onPostExecute(eventID);
            //handle result
            if (eventID!=-1) {
                UploadImage(eventID);
                Toast.makeText(AddEventActivity.this, "Event was added successfully", Toast.LENGTH_SHORT).show();
                finish();
                //need to update list... trigger push notification
            }
        }
    }

    private class UploadImageAsyncTask extends AsyncTask<Uri, Void, Integer> {
        int _eventID;

        UploadImageAsyncTask(int eventID) {
            _eventID = eventID;
        }

        @Override
        protected Integer doInBackground(Uri... uri) {
            try {
                final InputStream imageStream = getContentResolver().openInputStream(selectedImage);
                final int imageLength = imageStream.available();
                ImageManager.UploadImage(imageStream, imageLength, type + "_" + _eventID);
            } catch (Exception ex) {
                //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

        }

    }

    private void updateLocation(Place place) {
        EditText placeAddressEditText = (EditText) findViewById(R.id.add_event_edit_text_address);
        placeAddressEditText.setText(place.getAddress());
        //extract latitude and longtitude
        latitude = place.getLatLng().latitude;
        longtitude = place.getLatLng().longitude;
    }

    public void setDateAndTimeListener() {
        from_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                yearFinal = i;
                monthFinal = i1;
                dayFinal = i2;

                fromDate = new GregorianCalendar(yearFinal, monthFinal, dayFinal).getTime();
                //after pick the time
                openTimePicker(from_timeListener);
            }
        };

        from_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;

                Calendar cal = Calendar.getInstance();
                cal.setTime(fromDate);
                cal.add(Calendar.HOUR_OF_DAY, hour);
                cal.add(Calendar.MINUTE, minute);
                fromDate = cal.getTime();
                SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy HH:mm:00.00");
                Toast.makeText(AddEventActivity.this, ft.format(fromDate).toString(), Toast.LENGTH_SHORT).show();
                timeFromEditText.setText(ft.format(fromDate));
            }
        };

        to_dateListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                yearFinal = i;
                monthFinal = i1;
                dayFinal = i2;

                toDate = new GregorianCalendar(yearFinal, monthFinal, dayFinal).getTime();
                //after pick the time
                openTimePicker(to_timeListener);
            }
        };

        to_timeListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                int hour = i;
                int minute = i1;

                Calendar cal = Calendar.getInstance();
                cal.setTime(toDate);
                cal.add(Calendar.HOUR_OF_DAY, hour);
                cal.add(Calendar.MINUTE, minute);
                toDate = cal.getTime();
                SimpleDateFormat ft = new SimpleDateFormat("MM/dd/yyyy HH:mm:00.00");
                Toast.makeText(AddEventActivity.this, ft.format(toDate).toString(), Toast.LENGTH_SHORT).show();
                timeUntilEditText.setText(ft.format(toDate));
            }
        };
    }
}
