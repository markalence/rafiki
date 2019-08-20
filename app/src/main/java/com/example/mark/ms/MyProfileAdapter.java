package com.example.mark.ms;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProfileAdapter extends RecyclerView.Adapter<MyProfileAdapter.ViewHolder> {

    ArrayList<HashMap<String,Object>> mDataset;

    MyProfileAdapter(ArrayList<HashMap<String,Object>> dataset){
        mDataset = dataset;
        System.out.println(mDataset);
    }
    @NonNull
    @Override
    public MyProfileAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.day_item, parent, false);
        MyProfileAdapter.ViewHolder vh = new MyProfileAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull MyProfileAdapter.ViewHolder holder, int position) {

        holder.dayItemText.setText("Day: " +mDataset.get(position).get("day").toString()
        + "\n" + "Time:" + mDataset.get(position).get("time")
        + "\n" + "Hours: " + mDataset.get(position).get("hours"));

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

    TextView dayItemText;

    public ViewHolder(View itemView) {
        super(itemView);
        dayItemText = itemView.findViewById(R.id.dayItemText);
    }
}
}
