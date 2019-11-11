package com.rafiki.wits.sdp;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {
    @NonNull
    @Override
    public AnnouncementAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.announcement_item, parent, false);
        AnnouncementAdapter.ViewHolder vh = new AnnouncementAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull AnnouncementAdapter.ViewHolder holder, int position) {

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy");
        String courseCode = LoginActivity.announcements.get(position).get("courseCode").toString().toUpperCase();
        String text = LoginActivity.announcements.get(position).get("announcement").toString();
        String signOff = LoginActivity.announcements.get(position).get("studentNumber").toString();
        Timestamp timestamp = (Timestamp) LoginActivity.announcements.get(position).get("date");
        signOff += " on " + sdf.format(timestamp.toDate());
        holder.ann.setText("Course: " + courseCode + "\n" + "Announcement: " + text);
        holder.deets.setText(signOff);
    }

    @Override
    public int getItemCount() {
        return LoginActivity.announcements.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView ann;
        TextView deets;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            ann = itemView.findViewById(R.id.announceText);
            deets = itemView.findViewById(R.id.announcementDetails);
        }
    }
}
