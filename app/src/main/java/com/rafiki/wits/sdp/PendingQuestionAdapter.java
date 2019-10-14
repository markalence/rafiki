package com.rafiki.wits.sdp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
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

public class PendingQuestionAdapter extends RecyclerView.Adapter<PendingQuestionAdapter.ViewHolder> {

    ArrayList<String> mDataset;
    Context mContext;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ArrayList<String> selectedItems;
    private ArrayList<HashMap<String, Object>> copyItems;
    private ArrayList<Integer> indexList;
    private ArrayList<HashMap<String, Object>> copyDataset;
    Resources r;
    boolean clickable;


    public PendingQuestionAdapter(Context context, ArrayList<String> myDataset) {
        mDataset = myDataset;
        mContext = context;
        selectedItems = new ArrayList<>();
        copyItems = new ArrayList<>();
        indexList = new ArrayList<>();
        r = mContext.getResources();

    }

    @NonNull
    @Override
    public PendingQuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        PendingQuestionAdapter.ViewHolder vh = new PendingQuestionAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PendingQuestionAdapter.ViewHolder holder, int position) {


        holder.sessionLayout.setBackgroundResource(R.drawable.bottomline);
        holder.sessionInfo.setText(mDataset.get(position).toString().toUpperCase());
        holder.imageView.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
        holder.sessionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
        //holder.imageView.setPadding(20,20,20,20)

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView sessionInfo;
        String documentId;
        ConstraintLayout sessionLayout;
        ImageView imageView;
        String side;

        public ViewHolder(View itemView) {
            super(itemView);
            sessionInfo = itemView.findViewById(R.id.scheduleDetails);
            sessionInfo.setTypeface(null, Typeface.BOLD);
            sessionInfo.setTextColor(Color.WHITE);
            sessionLayout = itemView.findViewById(R.id.sessionLayout);
            imageView = itemView.findViewById(R.id.sessionInfo);
            side = "front";
        }
    }
}
