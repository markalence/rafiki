package com.rafiki.wits.sdp.Service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.RemoteInput;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.preference.PreferenceManager;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import android.util.Log;

import com.rafiki.wits.sdp.NotificationReceiver;
import com.rafiki.wits.sdp.R;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

public class FirebaseIDService extends FirebaseMessagingService {

    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    String firstName, lastName, username, grade;
    private SharedPreferences.Editor mEditor;
    public static Notification.Builder notificationBuilder;
    public static JSONObject j;


    @Override
    public void onNewToken(String s) {
        SharedPreferences mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        mEditor.putString("deviceToken", s);
        mEditor.commit();

        super.onNewToken(s);
        Log.d("NEW TOKEN: ", s);
    }

    private static int NOTIFICATION_ID = 0;


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        j = new JSONObject(remoteMessage.getData());
        final String RECORD_MODULE = "recordmodule";
        final String RECORD_HOURS = "recordhours";

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            RemoteInput remoteModule = new RemoteInput.Builder(RECORD_MODULE)
                    .setLabel("Module")
                    .build();

            RemoteInput remoteHours = new RemoteInput.Builder(RECORD_HOURS)
                    .setLabel("Modify Hours")
                    .build();

            Intent intent = new Intent(this, NotificationReceiver.class);
            intent.putExtra("documentData",j.toString());
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            Notification.Action moduleAction = new Notification.Action.Builder(
                    Icon.createWithResource(this, R.mipmap.ic_launcher_round),
                    "Record Module",
                    pendingIntent)
                    .addRemoteInput(remoteModule)
                    .build();

            Notification.Action hourAction = new Notification.Action.Builder(
                    Icon.createWithResource(this,R.drawable.ic_mode_edit_white_24dp),
                    "Change Hours",
                    pendingIntent)
                    .addRemoteInput(remoteHours)
                    .build();

           notificationBuilder = new Notification.Builder(getApplicationContext())
                    .setContentTitle("Please fill in your record sheet")
                    .setContentText(remoteMessage.getData().get("hours") + " hours")
                    .setContentIntent(pendingIntent)
                    .setColor(Color.rgb(25, 205, 205))
                    .setSmallIcon(R.mipmap.ic_launcher_round)
                    .addAction(moduleAction)
                   .setLights(Color.rgb(25,205,205),500,500)
                    .setDefaults(Notification.DEFAULT_ALL)
                   .setPriority(Notification.PRIORITY_MAX)
                    .addAction(hourAction);


            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
            notificationManagerCompat.notify(0, notificationBuilder.build());

        } else {

            Intent intent = new Intent(this, RecordSheetDialog.class);
            intent.putExtra("documentData", j.toString());
            System.out.println(j.toString());
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            if (android.os.Build.VERSION.SDK_INT == android.os.Build.VERSION_CODES.M) {
                System.out.println("THIS IS TRUE");

                Notification.Action action = new Notification.Action.Builder(
                        Icon.createWithResource(this, R.mipmap.ic_launcher_round),
                        "Record Module",
                        pendingIntent)
                        .build();

                Notification.Builder notification = new Notification.Builder(getApplicationContext())
                        .setContentTitle("Please fill in your record sheet.")
                        .setContentText(remoteMessage.getData().get("hours") + " hours")
                        .setContentIntent(pendingIntent)
                        .setColor(Color.rgb(25, 205, 205))
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .addAction(action)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setLights(Color.rgb(25,205,205),500,500)
                        .setPriority(Notification.PRIORITY_HIGH);

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(1, notification.build());
            } else {


                Intent mIntent = new Intent(this, RecordSheetDialog.class);
                mIntent.putExtra("documentData", j.toString());
                PendingIntent mPendingIntent = PendingIntent.getActivity(this, 2, mIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                NotificationCompat.Action action1 = new NotificationCompat.Action((R.drawable.ic_menu_send), "Record session", mPendingIntent);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, RECORD_MODULE)
                        .setSmallIcon(R.mipmap.ic_launcher_round)
                        .setContentTitle("Please fill in your record sheet.")
                        .setContentIntent(mPendingIntent)
                        .addAction(action1)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(NotificationCompat.PRIORITY_HIGH);

                Notification notification = mBuilder.build();

                NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
                notificationManagerCompat.notify(2, notification);
            }
        }

       }
    public void createNotificationChannel () {

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "mastermaths";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("channel", name, importance);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }

    }
}
//dDZpbYJC1hI:APA91bFuu2zrvSafzBehgr3EK4FifPTLiK9DwEmuDzvOxkIlwmlnWlqUo9EMKboVHcmMlxIaLLdmo2qkc7r1gcLNzbxFPnyO2XwpUoZxBet3pEOjrg1dOYtFbe3wvmBzNGNjViSYpgYa