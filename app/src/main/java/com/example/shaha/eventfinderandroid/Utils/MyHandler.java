package com.example.shaha.eventfinderandroid.Utils;

/**
 * Created by shaha on 14/02/2018.
 */

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

import com.example.shaha.eventfinderandroid.EventsMainActivity;
import com.example.shaha.eventfinderandroid.Fragments.UpcomingEventsFragment;
import com.example.shaha.eventfinderandroid.Login;
import com.example.shaha.eventfinderandroid.MainActivity;
import com.example.shaha.eventfinderandroid.MyEvent;
import com.example.shaha.eventfinderandroid.R;
import com.microsoft.windowsazure.notifications.NotificationsHandler;

import java.util.List;

public class MyHandler extends NotificationsHandler {
    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;
    Context ctx;

    @Override
    public void onReceive(Context context, Bundle bundle) {
        ctx = context;
        String nhMessage = bundle.getString("message");
        //check what type of message we received
        if (nhMessage.contains("event added")) {
            //refresh the event list in the upcomingFragment
            if (UpcomingEventsFragment.isVisible) {
                UpcomingEventsFragment.updateList();
                MainActivity.mainActivity.ToastNotify("An event was added");
            }
            sendNotification("An event was added");
        } else if (nhMessage.contains("joined event")) {
            String[] splits = nhMessage.split(" ");
            String eventIdStr = splits[3];
            int eventId = Integer.parseInt(eventIdStr);
            getEventTask task = new getEventTask();
            task.execute(eventId);
        }
//        if (MainActivity.isVisible) {
//            MainActivity.mainActivity.ToastNotify(nhMessage);
//        }
    }

    private class getEventTask extends AsyncTask<Integer, Void, MyEvent> {
        @Override
        protected MyEvent doInBackground(Integer... integers) {
            int eventId = integers[0];
            MyEvent myEvent = null;
            List<MyEvent> events = InternetUtils.getAllEvents();
            for (MyEvent event : events
                    ) {
                if (eventId == event.getEventID()) {
                    myEvent = event;
                }
            }
            return myEvent;
        }

        @Override
        protected void onPostExecute(MyEvent myEvent) {
            super.onPostExecute(myEvent);
            int userId = myEvent.getUserId();
            if (EventsMainActivity.getCurrentUser() == userId) {
                MainActivity.mainActivity.ToastNotify("Someone joined your event \nevent: " + myEvent.getEventName());
            }
        }
    }

    private void sendNotification(String msg) {

        Intent intent = new Intent(ctx, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        mNotificationManager = (NotificationManager)
                ctx.getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(ctx, 0,
                intent, PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(ctx)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Event was added")
                        .setStyle(new NotificationCompat.BigTextStyle()
                                .bigText(msg))
                        .setSound(defaultSoundUri)
                        .setContentText(msg);

        mBuilder.setContentIntent(contentIntent);
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
}
