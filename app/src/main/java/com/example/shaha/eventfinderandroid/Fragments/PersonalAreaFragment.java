package com.example.shaha.eventfinderandroid.Fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.shaha.eventfinderandroid.AddEventActivity;
import com.example.shaha.eventfinderandroid.EventAdapter;
import com.example.shaha.eventfinderandroid.EventInfoPopUpActivity;
import com.example.shaha.eventfinderandroid.EventsMainActivity;
import com.example.shaha.eventfinderandroid.MyEvent;
import com.example.shaha.eventfinderandroid.R;
import com.example.shaha.eventfinderandroid.Utils.InternetUtils;

import java.util.ArrayList;
import java.util.List;

public class PersonalAreaFragment extends Fragment {
    Context mContext;
    private ListView eventsListView;
    private EventAdapter mAdapter;
    private List<MyEvent> events = new ArrayList<>();

    public PersonalAreaFragment() {
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
        View view = inflater.inflate(R.layout.fragment_personal_area, container, false);
        //set the list view
        eventsListView = (ListView)view.findViewById(R.id.my_events_list_view);
        events = new ArrayList<>();
        mAdapter = new EventAdapter(getContext(), events);
        eventsListView.setAdapter(mAdapter);
        mContext = getContext();
        eventsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(mContext, EventInfoPopUpActivity.class);
                MyEvent event = mAdapter.getItem(i);
                intent.putExtra("event",event);
                startActivity(intent);
            }
        });

        //set an empty state for the listview
        LinearLayout emptyLayout = (LinearLayout)view.findViewById(R.id.personal_empty_state);
        eventsListView.setEmptyView(emptyLayout);

        //set the fab button
        FloatingActionButton addEvent = (FloatingActionButton)view.findViewById(R.id.fab);
        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intentAddEvent = new Intent(getContext(),AddEventActivity.class);
                startActivity(intentAddEvent);
            }
        });

        //get all the current user events
        EventsAsyncTask task = new EventsAsyncTask();
        task.execute();

        return view;
    }

    private class EventsAsyncTask extends AsyncTask<Integer,Void,List<MyEvent>>{

        @Override
        protected List<MyEvent> doInBackground(Integer... integers) {
            events = InternetUtils.getUserEvents(EventsMainActivity.getCurrentUser());
            return events;
        }

        @Override
        protected void onPostExecute(List<MyEvent> eventsList) {
            super.onPostExecute(events);

            //update the list view with the user events
            mAdapter.clear();
            mAdapter.addAll(eventsList);
            mAdapter.notifyDataSetChanged();
        }
    }
}
