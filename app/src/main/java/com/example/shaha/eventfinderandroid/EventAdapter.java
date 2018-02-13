package com.example.shaha.eventfinderandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by shaha on 08/01/2018.
 */

public class EventAdapter extends ArrayAdapter<MyEvent> {
    public EventAdapter(Context context, List<MyEvent> events) {
        super(context, 0, events);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.event_list_item, parent, false);
        }

        MyEvent curEvent = getItem(position);

        String eventName = curEvent.getEventName();
        String description = curEvent.getDescription();
        String fromDate = curEvent.getStartTime();
        String date = fromDate.substring(0, fromDate.indexOf(" "));
        String toDate = curEvent.getEndTime();
        String hours = fromDate.substring(fromDate.indexOf(" ") + 1, fromDate.indexOf(" ") + 1 + 4) + " - " + toDate.substring(toDate.indexOf(" ") + 1, toDate.indexOf(" ") + 1 + 4);

        TextView eventNameTv = (TextView) listItemView.findViewById(R.id.event_title_text_view);
        eventNameTv.setText(eventName);
        TextView descriptionTv = (TextView) listItemView.findViewById(R.id.last_msg_text_view);
        descriptionTv.setText(description);
        TextView eventDateTv = (TextView) listItemView.findViewById(R.id.list_item_event_date);
        eventDateTv.setText(date);
        TextView eventHoursTv = (TextView) listItemView.findViewById(R.id.list_item_event_time);
        eventHoursTv.setText(hours);

        return listItemView;
    }
}
