package com.rafiki.wits.sdp;

import androidx.annotation.Dimension;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;

public class SubscriptionActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    SubscriptionAdapter sa;
    Button confirm;
    Button cancel;
    TextView textView;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscription);
        db = FirebaseFirestore.getInstance();
        setRecyclerView();

        confirm = findViewById(R.id.subConfirm);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCourse();
            }
        });

        cancel = findViewById(R.id.subCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    //    public void addCourses(ArrayList<HashMap<String,Object> selectedItems){
//
//
//
//    }
    public void addCourse() {

        final ArrayList<Boolean> success = new ArrayList<>();
        success.add(true);

        for(String course : sa.getSelectedItems()) {
            LoginActivity.studentCourses.add(course);
        }
            db.collection("students")
                    .document(LoginActivity.studentNum)
                    .update("courses",LoginActivity.studentCourses)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(getBaseContext(), "You have successfully subscribed", Toast.LENGTH_LONG)
                                        .show();
                                addStudentToCourse();
                            }
                                else{
                                    Toast.makeText(getBaseContext(), "Error. Try again later", Toast.LENGTH_LONG)
                                            .show();
                                }
                        }
                    });


    }

    private void addStudentToCourse() {

        for (String course : sa.getSelectedItems()) {
            System.out.println("HEREEE " + course);
            db.collection("courses")
                    .whereEqualTo("courseCode", course)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful() && task.getResult().getDocuments().size() != 0) {
                                String id = task.getResult().getDocuments().get(0).getId();
                                HashMap<String,Object> student= new HashMap<>();
                                student.put("studentNumber",LoginActivity.studentNum);
                                db.collection("courses")
                                        .document(id)
                                        .collection("students")
                                        .add(student)
                                        .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentReference> task) {
                                                finish();
                                            }
                                        });
                            }
                        }
                    });
        }

    }

    public boolean setRecyclerView() {
        if (LoginActivity.interactionList != null) {
            recyclerView = findViewById(R.id.subscriptionList);
            DisplayMetrics displayMetrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            recyclerView.setMinimumWidth(width);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            sa = new SubscriptionAdapter(this, LoginActivity.courseCodes);
            recyclerView.setAdapter(sa);
            textView = findViewById(R.id.subTextView);
            textView.setTextSize(1, (float) (width * 0.015));
            return true;
        }
        return false;
    }
}
