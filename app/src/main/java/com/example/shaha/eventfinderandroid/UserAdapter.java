package com.example.shaha.eventfinderandroid;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by moran on 14/02/2018.
 */

public class UserAdapter extends ArrayAdapter<EventAttending> {
    public UserAdapter(Context context, List<EventAttending> attendings) {
        super(context, 0, attendings);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Check if there is an existing list item view (called convertView) that we can reuse,
        // otherwise, if convertView is null, then inflate a new list item layout.
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.user_list_item, parent, false);
        }

        EventAttending curUser = getItem(position);
        String fullName = curUser.getFullName();
        String phoneNumber = curUser.getPhoneNumber();
        TextView fullNameTv = (TextView) listItemView.findViewById(R.id.full_name_text_view);
        fullNameTv.setText(fullName);
        TextView phoneNumberTv = (TextView) listItemView.findViewById(R.id.phone_number_text_view);
        phoneNumberTv.setText(phoneNumber);

        return listItemView;
    }
}