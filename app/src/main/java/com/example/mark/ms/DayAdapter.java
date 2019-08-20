package com.example.mark.ms;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.common.SignInButton;

import java.util.ArrayList;
import java.util.HashMap;

public class DayAdapter extends RecyclerView.Adapter<DayAdapter.ViewHolder> {

    Context mContext;
    LayoutInflater mInflater;
    NumberPicker hourPicker;
    NumberPicker timePicker;
    ArrayList<HashMap<String, String>> copyDays;
    Resources r;

    DayAdapter(Context context, LayoutInflater inflater) {
        mContext = context;
        mInflater = inflater;
        r = mContext.getResources();
        copyDays = (ArrayList<HashMap<String, String>>) Login.userDays.clone();
    }

    @NonNull
    @Override
    public DayAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);
        DayAdapter.ViewHolder vh = new DayAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DayAdapter.ViewHolder holder, int position) {

        holder.setItem(position);

    }

    @Override
    public int getItemCount() {
        return Login.userDays.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView dayData;

        public ViewHolder(View itemView) {
            super(itemView);
            dayData = itemView.findViewById(R.id.dayItemText);


        }

        @SuppressLint("SetTextI18n")
        public void setItem(int position) {
            dayData.setText("Day: " + Login.userDays.get(position).get("day")
                    + "\n" + "Time: " + Login.userDays.get(position).get("time")
                    + "\n" + "Hours: " + Login.userDays.get(position).get("hours"));


        }



    }
}
