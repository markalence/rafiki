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

    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle remoteInput = RemoteInput.getResultsFromIntent(intent);

            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                CharSequence name = remoteInput.getCharSequence("rafiki");
                NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                mNotificationManager.cancel(1);

            }
        }
}
