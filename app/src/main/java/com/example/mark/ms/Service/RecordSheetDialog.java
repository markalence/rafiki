package com.example.mark.ms.Service;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.mark.ms.R;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RecordSheetDialog extends AppCompatActivity {
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    HashMap<String, Object> docData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_record_sheet_dialog);
        final EditText module = findViewById(R.id.module);
        final EditText hours = findViewById(R.id.hours);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        docData = new HashMap<>();

        if (getIntent().getExtras() != null) {
            try {
                JSONObject j = new JSONObject(getIntent().getExtras().getString("documentData"));
                hours.setText(j.get("hours").toString());
                docData.put("studentNum",j.get("studentNum").toString());
                docData.put("firstName",j.get("firstName").toString());
                docData.put("lastName",j.get("lastName").toString());
                Timestamp timestamp = new Timestamp(Long.valueOf(j.get("date").toString()),0);
                docData.put("date",timestamp);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        else{
            System.out.println("THEY NULL????");}

        Button confirm = findViewById(R.id.confirm);
        Button cancel = findViewById(R.id.cancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (module.getText().length() != 0 && hours.getText().length() != 0) {
                    docData.put("module",module.getText().toString());
                    docData.put("hours", hours.getText().toString());
                    firestore.collection("test")
                            .add(docData);
                    NotificationManager mNotificationManager = (NotificationManager)
                            getSystemService(Context.NOTIFICATION_SERVICE);
                    mNotificationManager.cancel(1);
                    mNotificationManager.cancel(2);
                    finish();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

}
