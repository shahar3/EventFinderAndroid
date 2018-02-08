package com.example.shaha.eventfinderandroid.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.shaha.eventfinderandroid.EventAdapter;
import com.example.shaha.eventfinderandroid.MyEvent;
import com.example.shaha.eventfinderandroid.R;
import com.example.shaha.eventfinderandroid.Utils.InternetUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UpcomingEventsFragment extends Fragment {
    private EventAdapter mAdapter;

    public UpcomingEventsFragment() {
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
        return inflater.inflate(R.layout.fragment_upcoming_events, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //initialize the list view
        List<MyEvent> events = new ArrayList<>();
        ListView eventsListView = (ListView)view.findViewById(R.id.upcoming_events_list_view);
        mAdapter = new EventAdapter(this.getContext(),events);
        eventsListView.setAdapter(mAdapter);

        EventsAsyncTask task = new EventsAsyncTask();
        task.execute();
    }

    private class EventsAsyncTask extends AsyncTask<Void, Void,List<MyEvent>> {

        @Override
        protected List<MyEvent> doInBackground(Void... voids) {
            //call the getAllEvents function
            List<MyEvent> events = InternetUtils.getAllEvents();

            return events;
        }

        @Override
        protected void onPostExecute(List<MyEvent> events) {
            super.onPostExecute(events);
            mAdapter.clear();
            mAdapter.addAll(events);
            mAdapter.notifyDataSetChanged();
        }
    }
}
