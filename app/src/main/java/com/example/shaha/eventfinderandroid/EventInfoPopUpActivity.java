
package com.example.shaha.eventfinderandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EventInfoPopUpActivity extends FragmentActivity{
    private TextView mTextView;
    private MyEvent curEvent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_popup);
        int[] width_height = getDimensions();
        //Set popup window to take portion of the screen.
        getWindow().setLayout((int) (width_height[0] * 0.8), (int) (width_height[1] * 0.7));
        mTextView = (TextView) findViewById(R.id.event_info_event_name_tv);

        //get the MepoEvent object
        Intent i = getIntent();
        curEvent = (MyEvent) i.getParcelableExtra("event");
        //show the event in the GUI
        updateUI(curEvent);

        //set join event button listener
        Button joinBtn = (Button)findViewById(R.id.event_info_join_btn);
        joinBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendJoinRequest();
            }
        });
    }

    private void sendJoinRequest() {
        //1. get user id
        int curUser = EventsMainActivity.getCurrentUser();
        //2. get event id
        String eventName = curEvent.getEventName();
        //3. send a post request to join event in a different thread
    }

    private void updateUI(MyEvent curEvent) {
        mTextView.setText(curEvent.getEventName());
    }

    //Get the device dimensions and return width and height
    private int[] getDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }



}