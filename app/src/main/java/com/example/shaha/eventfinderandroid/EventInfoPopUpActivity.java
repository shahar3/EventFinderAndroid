
package com.example.shaha.eventfinderandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.widget.TextView;

public class EventInfoPopUpActivity extends FragmentActivity{
    private TextView mTextView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_details_popup);
        int[] width_height = getDimensions();
        //Set popup window to take portion of the screen.
        getWindow().setLayout((int) (width_height[0] * 0.8), (int) (width_height[1] * 0.7));
        MyEvent curEvent;
        mTextView = (TextView) findViewById(R.id.event_info_event_name_tv);

        //get the MepoEvent object
        Intent i = getIntent();
        curEvent = (MyEvent) i.getParcelableExtra("event");
        //show the event in the GUI
        updateUI(curEvent);
    }

    private void updateUI(MyEvent curEvent) {
        TextView eventNameTextView = (TextView) findViewById(R.id.event_info_event_name_tv);
        eventNameTextView.setText(curEvent.getEventName());
    }

    //Get the device dimensions and return width and height
    private int[] getDimensions() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        return new int[]{displayMetrics.widthPixels, displayMetrics.heightPixels};
    }

}