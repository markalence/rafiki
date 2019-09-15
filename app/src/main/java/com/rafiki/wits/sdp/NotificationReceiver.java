package com.rafiki.wits.sdp;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Build;
import android.os.Bundle;

import androidx.core.app.RemoteInput;

import com.rafiki.wits.sdp.Service.FirebaseIDService;
import com.rafiki.wits.sdp.Service.RecordSheetDialog;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;

public class NotificationReceiver extends BroadcastReceiver {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String firstName, lastName, username, grade, hours;

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

        //if there is some input

        if(remoteInput.containsKey("recordhours")){
            FirebaseIDService.notificationBuilder
                    .setContentText(remoteInput.get("recordhours").toString() + " hours");
            try {
                FirebaseIDService.j.put("hours", remoteInput.get("recordhours").toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Notification notification = FirebaseIDService.notificationBuilder.build();
            notification.flags = Notification.FLAG_ONGOING_EVENT;
            NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.notify(0,notification);

        }

        else {

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                CharSequence name = remoteInput.getCharSequence("recordmodule");
                System.out.println(name);
                HashMap<String, Object> map = new HashMap<>();
                JSONObject j = null;
                try {
                    Resources r = context.getResources();
                    firstName = FirebaseIDService.j.getString(r.getString(R.string.FIRST_NAME));
                    lastName = FirebaseIDService.j.getString(r.getString(R.string.LAST_NAME));
                    username = FirebaseIDService.j.getString(r.getString(R.string.USERNAME));
                    hours = FirebaseIDService.j.getString(r.getString(R.string.HOURS));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                map.put("module", name);
                map.put("firstName",firstName);
                map.put("studentNum", username);
                map.put("lastName", lastName);
                map.put("hours", hours);
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.HOUR_OF_DAY, 12);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                map.put("date", calendar.getTime());
                firestore.collection("test").add(map);
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(0);

            } else {

                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);
                Intent nIntent = new Intent(context, RecordSheetDialog.class);
                nIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                nIntent.putExtra("documentData", intent.getExtras().get("documentData").toString());
                context.startActivity(nIntent);

            }
        }

    }
}
