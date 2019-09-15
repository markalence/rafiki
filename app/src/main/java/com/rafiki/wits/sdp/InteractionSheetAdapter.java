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

public class InteractionSheetAdapter extends RecyclerView.Adapter<InteractionSheetAdapter.ViewHolder> {

    private ArrayList<HashMap<String, Object>> mDataset;
    private Context mContext;
    private String dateFormat = "dd MMMM";
    private SimpleDateFormat sfd = new SimpleDateFormat(dateFormat);
    Resources r;

    public InteractionSheetAdapter(Context context, ArrayList<HashMap<String, Object>> myDataset) {
        this.mDataset = myDataset;
        this.mContext = context;
        r = this.mContext.getResources();
    }

    @Override
    public InteractionSheetAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                 int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.record_sheet_item_fancy, parent, false);
        InteractionSheetAdapter.ViewHolder vh = new InteractionSheetAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(InteractionSheetAdapter.ViewHolder holder, int position) {

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
                            item.get(r.getString(R.string.MODULE)).toString());

            this.commentRecord.setText("Tutor Response: " +
                    item.get(r.getString(R.string.COMMENT)).toString());

            Timestamp timestamp = (Timestamp) item.get(r.getString(R.string.DATE));
            Date sessionDate = timestamp.toDate();
            dateRecord.setText(sfd.format(sessionDate));
        }
    }

}


