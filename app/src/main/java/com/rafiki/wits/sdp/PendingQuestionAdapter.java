package com.rafiki.wits.sdp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class PendingQuestionAdapter extends RecyclerView.Adapter<PendingQuestionAdapter.ViewHolder> {

    ArrayList<HashMap<String, Object>> mDataset;
    Context mContext;
    LayoutInflater inflater;
    FirebaseFirestore firestore = FirebaseFirestore.getInstance();
    Button confirm;
    Button cancel;
    TextView answerText;
    FirebaseFirestore db;

    private ArrayList<String> selectedItems;
    private ArrayList<HashMap<String, Object>> copyItems;
    private ArrayList<Integer> indexList;
    private ArrayList<HashMap<String, Object>> copyDataset;
    Resources r;
    private AlertDialog recordDialog;
    private String DATE_FORMAT = "dd MMMM";
    SimpleDateFormat sfd = new SimpleDateFormat(DATE_FORMAT);


    public PendingQuestionAdapter(Context context, LayoutInflater inflater, ArrayList<HashMap<String, Object>> myDataset) {
        mDataset = myDataset;
        mContext = context;
        this.inflater = inflater;
        selectedItems = new ArrayList<>();
        copyItems = new ArrayList<>();
        indexList = new ArrayList<>();
        r = mContext.getResources();
        db = FirebaseFirestore.getInstance();
        final AlertDialog.Builder recordBuilder = new AlertDialog.Builder(mContext);
        View v = inflater.inflate(R.layout.answer_layout, null);
        recordBuilder.setView(v);
        confirm = v.findViewById(R.id.moduleConfirm);
        cancel = v.findViewById(R.id.moduleCancel);
        answerText = v.findViewById(R.id.answerText);
        recordDialog = recordBuilder.create();
        recordDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.dismiss();
            }
        });

    }

    @NonNull
    @Override
    public PendingQuestionAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.question_item, parent, false);
        PendingQuestionAdapter.ViewHolder vh = new PendingQuestionAdapter.ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final PendingQuestionAdapter.ViewHolder holder, final int position) {

        Timestamp ts = (Timestamp) mDataset.get(position).get("date");
        Date dateRecord = ts.toDate();
        String date = sfd.format(dateRecord);

        String sourceString = "<b>" + date + "</b> " + "<br>" +
                "Course: " + mDataset.get(position).get("courseCode").toString().toUpperCase()
                + "<br>" + "Student Number: " + mDataset.get(position).get("studentNumber")
                + "<br>" + "Question: " + mDataset.get(position).get("question");
        holder.qlayout.setBackgroundResource(R.drawable.bottomline);
        holder.qdetails.setText(Html.fromHtml(sourceString));

        holder.qanswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                recordDialog.show();
                confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        HashMap<String, Object> ans = mDataset.get(position);
                        ans.put("answer", answerText.getText().toString());
                        answerText.setText("");
                        mDataset.remove(position);
                        notifyItemRemoved(position);
                        sendAnswer(ans);
                    }
                });
            }
        });

    }

    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<String> getSelectedItems() {
        return selectedItems;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView qdetails;
        ImageButton qanswer;
        ConstraintLayout qlayout;

        public ViewHolder(View itemView) {
            super(itemView);
            qdetails = itemView.findViewById(R.id.qdetails);
            qanswer = itemView.findViewById(R.id.qanswer);
            qlayout = itemView.findViewById(R.id.qlayout);
        }
    }

    public void sendAnswer(HashMap<String, Object> ans) {

        db.collection("answeredquestions")
                .add(ans);

        db.collection("pendingquestions")
                .whereEqualTo("question", ans.get("question"))
                .whereEqualTo("date", ans.get("date"))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                db.collection("pendingquestions")
                                        .document(doc.getId())
                                        .delete();
                            }
                        }
                    }
                });
        Toast.makeText(mContext, "Answer sent", Toast.LENGTH_LONG).show();
        recordDialog.dismiss();
    }

}
