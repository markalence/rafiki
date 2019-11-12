package com.rafiki.wits.sdp;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class QuestionSubmitter {


    private Context mContext;
    private LayoutInflater mInflater;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private AlertDialog recordDialog;
    String courseSelection;
    View moduleView;


    private HashMap<String, Object> docData = new HashMap<>();

    QuestionSubmitter(Context context, LayoutInflater inflater) {
        mInflater = inflater;
        mContext = context;
    }

    public boolean getCourseSelection(){

        moduleView = mInflater.inflate(R.layout.course_spinner,null);
        final Button confirm = moduleView.findViewById(R.id.spinnerConfirm);
        final Button cancel = moduleView.findViewById(R.id.spinnerCancel);
        final Spinner spinner = moduleView.findViewById(R.id.spinner);
        final AlertDialog.Builder recordBuilder = new AlertDialog.Builder(mContext);
        recordBuilder.setView(moduleView);
        recordDialog = recordBuilder.create();
        recordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ArrayList<String> courseCaps = new ArrayList<>();
        for(String s : LoginActivity.courseCodes){
            courseCaps.add(s.toUpperCase());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                mContext,
                android.R.layout.simple_spinner_item,
                courseCaps
        );
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                courseSelection = LoginActivity.courseCodes.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(courseSelection == null){
                    Toast.makeText(mContext, "Please select a course", Toast.LENGTH_SHORT);
                }
                else{
                    recordDialog.dismiss();
                    questionDialogInit();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
            }
        });

        recordDialog.show();

        return true;
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
                docData.put("date", new Timestamp(new Date()));
                docData.put("courseCode",courseSelection);
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
