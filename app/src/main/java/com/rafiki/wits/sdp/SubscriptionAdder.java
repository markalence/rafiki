package com.rafiki.wits.sdp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionAdder {

    RecyclerView recyclerView;
    SubscriptionAdapter sa;
    Button confirm;
    Button cancel;
    TextView textView;
    WindowManager wm;
    Context context;
    View view;
    FirebaseFirestore db;
    Dialog dialog;

    SubscriptionAdder(Context context, LayoutInflater inflater, WindowManager wm) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        view = inflater.inflate(R.layout.activity_subscription, null);
        db = FirebaseFirestore.getInstance();
        this.context = context;
        this.wm = wm;
        builder.setView(view);
        setRecyclerView();
        dialog = builder.create();
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        confirm = view.findViewById(R.id.subConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });

        cancel = view.findViewById(R.id.subCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    public boolean setRecyclerView() {
        if (LoginActivity.interactionList != null) {
            recyclerView = view.findViewById(R.id.subscriptionList);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            wm.getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            recyclerView.setMinimumWidth(width);
            recyclerView.setLayoutManager(new LinearLayoutManager(context));
            sa = new SubscriptionAdapter(context, LoginActivity.courseCodes);
            recyclerView.setAdapter(sa);
            textView = view.findViewById(R.id.subTextView);
            textView.setTextSize(1, (float) (width * 0.015));
            return true;
        }
        return false;
    }

    //    public void addCourses(ArrayList<HashMap<String,Object> selectedItems){
//
//
//
//    }
    public void addCourse() {
        System.out.println(sa.getSelectedItems() + " HHEEERRE");

        if (sa.getSelectedItems().size() == 0) {
            dialog.dismiss();
            return;
        }
        for (String course : sa.getSelectedItems()) {
            if (!LoginActivity.studentCourses.contains(course)) {
                LoginActivity.studentCourses.add(course);
            }
        }
        db.collection("students")
                .document(LoginActivity.studentNum)
                .update("courses", LoginActivity.studentCourses)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(context, "You have successfully subscribed", Toast.LENGTH_LONG)
                                    .show();
                            dialog.hide();
                            addStudentToCourse();
                        } else {
                            Toast.makeText(context, "Error. Try again later", Toast.LENGTH_LONG)
                                    .show();
                            dialog.hide();
                        }
                    }
                });


    }

    private void addStudentToCourse() {

        for (String course : sa.getSelectedItems()) {
            db.collection("courses")
                    .whereEqualTo("courseCode", course)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getDocuments().size() != 0) {
                                String id = task.getResult().getDocuments().get(0).getId();
                                HashMap<String, Object> student = new HashMap<>();
                                student.put("studentNumber", LoginActivity.studentNum);
                                db.collection("courses")
                                        .document(id)
                                        .collection("students")
                                        .add(student)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                dialog.hide();
                                            }
                                        });
                            }
                        }
                    });
        }

    }

}
