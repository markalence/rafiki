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

public class SubscriptionAdapter extends RecyclerView.Adapter<SubscriptionAdapter.ViewHolder> {

    ArrayList<String> mDataset;
    Context mContext;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();

    private ArrayList<String> selectedItems = new ArrayList<>();
    private ArrayList<HashMap<String, Object>> copyItems;
    private ArrayList<Integer> indexList;
    private ArrayList<HashMap<String,Object>> copyDataset;
    Resources r;
    boolean clickable;


    public SubscriptionAdapter(Context context, ArrayList<String> myDataset) {
        mDataset = myDataset;
        mContext = context;
        selectedItems = new ArrayList<>();
        copyItems = new ArrayList<>();
        indexList = new ArrayList<>();
        r = mContext.getResources();

    }

    @NonNull
    @Override
    public SubscriptionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.session_item, parent, false);
        SubscriptionAdapter.ViewHolder vh = new SubscriptionAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final SubscriptionAdapter.ViewHolder holder, int position) {


        holder.sessionLayout.setBackgroundResource(R.drawable.bottomline);
        holder.sessionInfo.setText(mDataset.get(position).toString().toUpperCase());
        showFrontOfDrawable(holder,holder.getAdapterPosition());

        holder.imageView.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
        holder.sessionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final ObjectAnimator oa1 = ObjectAnimator.ofFloat(holder.imageView, "scaleY", 1f, 0f);
                oa1.setDuration(45);
                final ObjectAnimator oa2 = ObjectAnimator.ofFloat(holder.imageView, "scaleY", 0f, 1f);
                oa2.setDuration(45);
                oa1.setInterpolator(new DecelerateInterpolator());
                oa2.setInterpolator(new AccelerateDecelerateInterpolator());

                oa1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (holder.side.equals("front")) {

                            showBackOfDrawable(holder, holder.getAdapterPosition());

                        } else {

                            showFrontOfDrawable(holder, holder.getAdapterPosition());

                        }
                        oa2.start();
                    }
                });
                oa1.start();
            }
        });
        //holder.imageView.setPadding(20,20,20,20)

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<String> getSelectedItems(){
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
            sessionInfo.setTypeface(null,Typeface.BOLD);
            sessionInfo.setTextColor(Color.WHITE);
            sessionLayout = itemView.findViewById(R.id.sessionLayout);
            imageView = itemView.findViewById(R.id.sessionInfo);
            side = "front";
        }
    }


    public void showBackOfDrawable(ViewHolder holder, int position) {

        holder.sessionLayout.setBackgroundResource(R.drawable.bottomline);
        holder.imageView.setImageResource(R.drawable.ic_radio_button_checked_white_24dp);
        holder.sessionLayout.setBackgroundColor(Color.rgb(200, 200, 200));
        holder.imageView.setPadding(30, 30, 30, 30);
        if(!LoginActivity.studentCourses.contains(mDataset.get(position))){
            selectedItems.add(mDataset.get(position));

        }
        holder.side = "back";


    }

    public void showFrontOfDrawable(ViewHolder holder, int position) {

        holder.sessionLayout.setBackgroundResource(R.drawable.bottomline);
        holder.sessionLayout.setBackgroundColor(Color.rgb(150, 150, 150));
        holder.imageView.setImageResource(R.drawable.ic_radio_button_unchecked_white_24dp);
        holder.imageView.setBackgroundColor(Color.TRANSPARENT);
        holder.imageView.setPadding(30, 30, 30, 30);
        holder.side = "front";
        if (!selectedItems.isEmpty()) {
            selectedItems.remove(mDataset.get(position));
        }
    }
}
