
package com.example.shaha.eventfinderandroid;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.shaha.eventfinderandroid.Adapters.UserAdapter;
import com.example.shaha.eventfinderandroid.Utils.ImageManager;
import com.example.shaha.eventfinderandroid.Utils.InternetUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class EventInfoPopUpActivity extends FragmentActivity {
    private TextView mTextView;
    private MyEvent curEvent;
    private boolean showBtn;
    private ByteArrayOutputStream imageStream;
    private ImageView imageView;
    private UserAdapter mAdapter;
    private List<EventUser> attendings = new ArrayList<>();
    private Context mContext = this;
    private TextView mAdressTv;
    private ListView attendingListView;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_popup);
        int[] width_height = getDimensions();
        //Set popup window to take portion of the screen.
        getWindow().setLayout((int) (width_height[0] * 0.8), (int) (width_height[1] * 0.7));

        //set the list view
        attendingListView = (ListView) findViewById(R.id.event_info_attendings_list_view);

        //get the MepoEvent object
        Intent i = getIntent();
        curEvent = (MyEvent) i.getParcelableExtra("event");
        showBtn = i.getBooleanExtra("showJoinBtn", false);

        getAttendings();
        DownloadImage();

        //show the event in the GUI
        updateUI(curEvent);

        //set join event button listener
        Button joinBtn = (Button) findViewById(R.id.event_info_join_btn);
        //check if join button is needed
        if (!showBtn) {
            joinBtn.setVisibility(View.GONE);
        }

        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendJoinRequest();
            }
        });

        //set chat button listener
        ImageButton chatBtn = (ImageButton) findViewById(R.id.event_info_chat_btn);
        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openChat();
            }
        });
    }

    private void openChat() {
    }

    private void getAttendings() {
        EventAttendingsAsyncTask task = new EventAttendingsAsyncTask();
        task.execute();
    }

    private class EventAttendingsAsyncTask extends AsyncTask<Void, Void, List<EventUser>> {

        @Override
        protected List<EventUser> doInBackground(Void... vo) {
            attendings = InternetUtils.getEventAttendings(curEvent.getEventID());
            return attendings;
        }

        @Override
        protected void onPostExecute(List<EventUser> attendings) {
            TextView textViewAttendings = (TextView) findViewById(R.id.event_info_attendings_count);
            if (attendings.size() != 0) {
                textViewAttendings.setText((attendings.size()) + "");
                mAdapter = new UserAdapter(mContext, attendings);
                attendingListView.setAdapter(mAdapter);
            } else {
                textViewAttendings.setText("0");
            }
        }

    }

    private void DownloadImage() {
        imageView = (ImageView) findViewById(R.id.event_img);
        final String imageName = curEvent.getEventName();
        imageStream = new ByteArrayOutputStream();
        DownloadImageAsyncTask task = new DownloadImageAsyncTask(imageName);
        task.execute(imageStream);
    }

    private class DownloadImageAsyncTask extends AsyncTask<ByteArrayOutputStream, Void, Integer> {
        String _imageName;

        DownloadImageAsyncTask(String imageName) {
            _imageName = imageName;
        }

        @Override
        protected Integer doInBackground(ByteArrayOutputStream... stream) {
            try {
                long imageLength = 0;
                ImageManager.GetImage(_imageName, imageStream, imageLength);
                byte[] buffer = imageStream.toByteArray();
                Bitmap bitmap = BitmapFactory.decodeByteArray(buffer, 0, buffer.length);
                imageView.setImageBitmap(bitmap);
            } catch (Exception ex) {
                //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
            }
            return 1;
        }

        @Override
        protected void onPostExecute(Integer integer) {

        }

    }


    private void sendJoinRequest() {
        //1. get user id
        int curUser = EventsMainActivity.getCurrentUser();
        //2. get event id
        int eventID = curEvent.getEventID();
        //3. send a post request to join event in a different thread
        JoinEventAsyncTask task = new JoinEventAsyncTask(eventID, curUser);
        task.execute();
    }

    private class JoinEventAsyncTask extends AsyncTask<Void, Void, Boolean> {
        int _eventID;
        int _curUser;

        JoinEventAsyncTask(int eventID, int curUser) {
            _eventID = eventID;
            _curUser = curUser;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            //call the joinEvent function
            boolean isJoin = InternetUtils.joinEvent(_eventID, _curUser);
            return isJoin;
        }

        protected void onPostExecute(Boolean isJoin) {
            super.onPostExecute(isJoin);
            //handle result
            if (isJoin) {
                Toast.makeText(EventInfoPopUpActivity.this, "Join event successfully", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    private void updateUI(MyEvent curEvent) {
        mTextView = (TextView) findViewById(R.id.event_info_event_name_tv);
        mTextView.setText(curEvent.getEventName());
        updateAddress();
    }

    private void updateAddress() {
        updateAddressAsyncTask task = new updateAddressAsyncTask();
        task.execute();
    }

    private class updateAddressAsyncTask extends AsyncTask<Void, Void, String> {

        @Override
        protected String doInBackground(Void... voids) {
            Geocoder gc = new Geocoder(mContext, Locale.getDefault());
            List<Address> addresses;
            String address = "";
            try {
                addresses = gc.getFromLocation(curEvent.getLatitude(), curEvent.getLongtitude(), 1);
                if (addresses != null && addresses.size() > 0) {
                    address = addresses.get(0).getAddressLine(0);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return address;
        }

        @Override
        protected void onPostExecute(String address) {
            mAdressTv = (TextView) findViewById(R.id.event_info_address);
            mAdressTv.setText(address);
        }
    }


    //Get the device dimensions and return width and height
    private int[] getDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }


}