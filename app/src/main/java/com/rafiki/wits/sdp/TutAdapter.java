package com.rafiki.wits.sdp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class TutAdapter extends RecyclerView.Adapter<TutAdapter.ViewHolder> {

    ArrayList<HashMap<String, Object>> mDataset;
    Context mContext;
    private String DATE_FORMAT_SHORT = "dd/MM";
    private String DATE_FORMAT_LONG = "EEEE, MMMM dd";
    private String HOUR_FORMAT = "kk:mm";
    Resources r;
    SimpleDateFormat sdfs = new SimpleDateFormat(DATE_FORMAT_SHORT);
    SimpleDateFormat sdfl = new SimpleDateFormat(DATE_FORMAT_LONG);
    SimpleDateFormat sdfh = new SimpleDateFormat(HOUR_FORMAT);


    public TutAdapter(Context context, ArrayList<HashMap<String, Object>> myDataset) {
        mDataset = myDataset;
        if (myDataset == null) {
            mDataset = new ArrayList<>();
        }
        mContext = context;
        r = mContext.getResources();

    }

    @NonNull
    @Override
    public TutAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        TutAdapter.ViewHolder vh = new TutAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final TutAdapter.ViewHolder holder, int position) {

        holder.sessionLayout.setBackgroundResource(R.drawable.bottomline);
        final Timestamp sTimestamp = (Timestamp) mDataset.get(position).get("startTime");
        final Date sdateRecord = sTimestamp.toDate();
        final Timestamp eTimestamp = (Timestamp) mDataset.get(position).get("endTime");
        final Date edateRecord = eTimestamp.toDate();
        long diff = edateRecord.getTime() - sdateRecord.getTime();
        long seconds = diff / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        String duration;
        if (minutes < 60) {
            duration = minutes + " minutes";
        } else {
            duration = minutes % 60 == 0 ? (int) hours + " hours" : (int) hours + " hour(s) and " + minutes % 60 + " minutes";
        }
        final String dateStringShort = sdfs.format(sdateRecord);
        final TextDrawable drawableFront = TextDrawable.builder()
                .beginConfig()
                .fontSize((int) ((18 * Resources.getSystem().getDisplayMetrics().density)))
                .endConfig()
                .buildRound(dateStringShort, ColorGenerator.MATERIAL.getRandomColor());
        drawableFront.setPadding(30, 30, 30, 30);
        holder.sessionInfo.setText(mDataset.get(position).get("courseCode").toString().toUpperCase()
                + "\n"
                + sdfl.format(sdateRecord)
                + "\n"
                + "Starts at: " + sdfh.format(sdateRecord)
                + "\nDuration: " + duration

        );
        holder.imageView.setImageDrawable(drawableFront);
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sessionInfo;
        ConstraintLayout sessionLayout;
        ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            sessionInfo = itemView.findViewById(R.id.scheduleDetails);
            sessionLayout = itemView.findViewById(R.id.sessionLayout);
            imageView = itemView.findViewById(R.id.sessionInfo);
        }
    }

}
