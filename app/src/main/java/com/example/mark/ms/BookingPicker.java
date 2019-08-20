package com.example.mark.ms;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class BookingPicker implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private int year, month, day;
    private int yearFinal, monthFinal, dayFinal, hourFinal, minuteFinal;
    private Context mContext;
    private DatePickerDialog mDatePickerDialog;
    private TimePickerDialog mTimePickerDialog;
    private LayoutInflater mInflater;
    private FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    private AlertDialog.Builder builder;
    private View dialogView;
    private AlertDialog alertDialog = null;
    private boolean bookingOnSameDay = false;
    private boolean dialogCanceled = false;
    private static HashMap<String, Object> docData = new HashMap<>();
    private final SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
    private Resources r;


    BookingPicker(Context context, LayoutInflater inflater) {

        Calendar calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        mContext = context;
        mInflater = inflater;
        dialogView = mInflater.inflate(R.layout.booking_hours, null);
        builder = new AlertDialog.Builder(mContext);
        builder.setView(dialogView);
        alertDialog = builder.create();
        r = mContext.getResources();

        mDatePickerDialog = new DatePickerDialog(mContext,
                R.style.CustomDialogTheme,
                BookingPicker.this,
                year, month, day);

        mDatePickerDialog.getDatePicker().setMinDate(calendar.getTime().getTime());
        calendar.add(Calendar.MONTH,1);
        mDatePickerDialog.getDatePicker().setMaxDate(calendar.getTime().getTime());
        mDatePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialogCanceled = true;
                mDatePickerDialog.dismiss();
                docData.clear();
            }
        });

        View dateTitleView = mInflater.inflate(R.layout.date_picker_title,null);
        mDatePickerDialog.setCustomTitle(dateTitleView);



        mTimePickerDialog = new TimePickerDialog(mContext, R.style.CustomDialogTheme,
                BookingPicker.this,
                14, 0,
                false);

        View timeTitleView = mInflater.inflate(R.layout.time_picker_title,null);
        mTimePickerDialog.setCustomTitle(timeTitleView);


        mTimePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "CANCEL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                mTimePickerDialog.dismiss();
                docData.clear();
                dialogCanceled = true;
            }
        });

    }

    public void makeBooking() {
        mDatePickerDialog.show();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        yearFinal = year;
        monthFinal = month;
        dayFinal = dayOfMonth;
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, year);
        calendar.set(Calendar.MONTH, month);
        calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        Date date = calendar.getTime();
        Timestamp bookingTimestamp = new Timestamp(date);
        bookingOnSameDay = false;
        checkDateIsValid(bookingTimestamp);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        hourFinal = hourOfDay;
        minuteFinal = minute;

        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, yearFinal);
        cal.set(Calendar.MONTH, monthFinal);
        cal.set(Calendar.DAY_OF_MONTH, dayFinal);
        cal.set(Calendar.HOUR_OF_DAY, hourFinal);
        cal.set(Calendar.MINUTE, minuteFinal);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        Date bookingDate = cal.getTime();
        Timestamp bookingTimestamp = new Timestamp(bookingDate);
        docData.put(r.getString(R.string.DATE), bookingTimestamp);
        if(!dialogCanceled){numberPickerInit();}
    }


    public void checkDateIsValid(final Timestamp timestamp) {

        Date todayDate = new Date();
        Timestamp todayTimestamp = new Timestamp(todayDate);


        final Date calenderDate = timestamp.toDate(); // date being booked

        for (HashMap<String, Object> doc : Login.upcomingSessions) {
            Timestamp sessionTimestamp = (Timestamp) doc.get(r.getString(R.string.DATE));
            Date sessionDate = sessionTimestamp.toDate(); //date being checked
            if (fmt.format(sessionDate).equals(fmt.format(calenderDate))) {
                Toast.makeText(mContext, "You already have a session on this day!", Toast.LENGTH_SHORT).show();
                bookingOnSameDay = true;
                docData.clear();
                mTimePickerDialog.dismiss();
                mDatePickerDialog.show();
                break;
            }
        }
        if (!bookingOnSameDay && !dialogCanceled) {
            mTimePickerDialog.show();
        }
    }

    private void numberPickerInit() {

        Button confirm = dialogView.findViewById(R.id.hourConfirm);
        Button cancel = dialogView.findViewById(R.id.hourCancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.dismiss();
                docData.put(r.getString(R.string.FIRST_NAME), Login.firstName);
                docData.put(r.getString(R.string.LAST_NAME), Login.lastName);
                docData.put(r.getString(R.string.USERNAME), Login.studentNum);
                uploadToSchedule();
            }
        });


        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

        docData.put(r.getString(R.string.HOURS), 1);
        final NumberPicker numberPicker = dialogView.findViewById(R.id.numberPicker);
        numberPicker.setMinValue(0);
        numberPicker.setMaxValue(6);
        numberPicker.setWrapSelectorWheel(true);
        numberPicker.setDisplayedValues(r.getStringArray(R.array.hourArray));
        numberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldValue, int newValue) {
                docData.put(r.getString(R.string.HOURS), r.getStringArray(R.array.hourArray)[newValue]);
            }

        });
        alertDialog.show();
    }

    private void uploadToSchedule() {

        Timestamp existing;
        Timestamp booking = (Timestamp) docData.get(r.getString(R.string.DATE));

        for (int i = 0; i < Login.upcomingSessions.size(); ++i) {
            existing = (Timestamp) Login.upcomingSessions.get(i).get(r.getString(R.string.DATE));
            if (existing.getSeconds() > booking.getSeconds()) {
                Login.upcomingSessions.add(i, (HashMap<String, Object>) docData.clone());
                break;
            }
            System.out.println(i);
            if(i==(Login.upcomingSessions.size()-1)){
                Login.upcomingSessions.add((HashMap<String, Object>) docData.clone());
                break;
            }
        }

        if(Login.upcomingSessions.isEmpty()){
            Login.upcomingSessions.add((HashMap<String, Object>) docData.clone());
        }

        firestore.collection(r.getString(R.string.SCHEDULE)).add(docData).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
            @Override
            public void onComplete(@NonNull Task<DocumentReference> task) {
                if (task.isSuccessful()) {
                    docData.clear();
                    Toast.makeText(mContext, "Session booked!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "Couldn't book a session at this time.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
