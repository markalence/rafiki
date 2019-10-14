package com.rafiki.wits.sdp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;


public class QuestionSubmitter {


    private Context mContext;
    private LayoutInflater mInflater;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private AlertDialog recordDialog;


    private HashMap<String, Object> docData = new HashMap<>();

    QuestionSubmitter(Context context, LayoutInflater inflater) {
        mInflater = inflater;
        mContext = context;
    }


    public boolean questionDialogInit() {
        final View moduleView = mInflater.inflate(R.layout.record_module, null);
        final Button moduleCancel = moduleView.findViewById(R.id.moduleCancel);
        final Button moduleConfirm = moduleView.findViewById(R.id.moduleConfirm);
        final EditText et = moduleView.findViewById(R.id.moduleText);
        final AlertDialog.Builder recordBuilder = new AlertDialog.Builder(mContext);
        recordBuilder.setView(moduleView);
        recordDialog = recordBuilder.create();

        moduleConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT <= 20) {
                    recordDialog.dismiss();
                }
                docData.put("studentNumber", LoginActivity.studentNum);
                docData.put("question",et.getText().toString());
                firestore.collection("pendingquestions").add(docData);
                recordDialog.dismiss();
                Toast.makeText(mContext, "Thank you! Your question has been submitted.", Toast.LENGTH_SHORT).show();
            }
        });

        moduleCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
            }
        });
        recordDialog.show();

        return true;
    }

}
