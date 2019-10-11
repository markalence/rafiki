package com.rafiki.wits.sdp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class InteractionListAdapter extends RecyclerView.Adapter<InteractionListAdapter.ViewHolder> {

    private ArrayList<HashMap<String, Object>> mDataset;
    private Context mContext;
    private String dateFormat = "dd MMMM";
    private SimpleDateFormat sfd = new SimpleDateFormat(dateFormat);
    Resources r;

    public InteractionListAdapter(Context context, ArrayList<HashMap<String, Object>> myDataset) {
        this.mDataset = myDataset;
        this.mContext = context;
        r = this.mContext.getResources();
    }

    @Override
    public InteractionListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_sheet_item_fancy, parent, false);
        InteractionListAdapter.ViewHolder vh = new InteractionListAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InteractionListAdapter.ViewHolder holder, int position) {

        holder.setItem(mDataset.get(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView dateRecord;
        TextView commentRecord;
        TextView moduleRecord;

        public ViewHolder(View v) {
            super(v);
            dateRecord = v.findViewById(R.id.dateRecord);
            commentRecord = v.findViewById(R.id.commentRecord);
            moduleRecord = v.findViewById(R.id.moduleRecord);
        }

        @SuppressLint("SetTextI18n")
        public void setItem(HashMap<String, Object> item) {

            this.moduleRecord.setText("Question: " +
                            item.get("question").toString());

            this.commentRecord.setText("Tutor Response: " +
                    item.get("answer").toString());

            Timestamp timestamp = (Timestamp) item.get(r.getString(R.string.DATE));
            Date sessionDate = timestamp.toDate();
            dateRecord.setText(sfd.format(sessionDate));
        }
    }

}


