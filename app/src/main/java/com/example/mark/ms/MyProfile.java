package com.example.mark.ms;

import android.os.Bundle;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.ArrayList;
import java.util.HashMap;

public class MyProfile extends AppCompatActivity {

    FirebaseFirestore firestore;
    private String STUDENT_CELL = "studentCell";
    private String FATHER_CELL = "fatherCell";
    private String MOTHER_CELL = "motherCell";
    private String HOME_NUMBER = "homeNumber";
    private String EMAIL = "email";
    private EditText studentCell;
    private EditText fatherCell;
    private EditText motherCell;
    private EditText homeNumber;
    private EditText email;
    private RecyclerView recyclerView;
    public static ArrayList<HashMap<String,Object>> dayData = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        /*studentCell = findViewById(R.id.studentCell);
        fatherCell = findViewById(R.id.fatherCell);
        motherCell = findViewById(R.id.motherCell);
        homeNumber = findViewById(R.id.homeNumber);
        email = findViewById(R.id.email);
        recyclerView = findViewById(R.id.profileRecyclerView);*/
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        firestore = FirebaseFirestore.getInstance();
        firestore.collection("students")
                .document(Login.studentNum)
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                        if(task.isSuccessful()){

                            DocumentSnapshot doc = task.getResult();

                            String[] stringArr = {STUDENT_CELL,FATHER_CELL,MOTHER_CELL,HOME_NUMBER,EMAIL};
                            EditText[] etArr = {studentCell,fatherCell,motherCell,homeNumber,email};

                            for(int i = 0; i<stringArr.length; ++i){
                                if(doc.get(stringArr[i])!=null && doc.get(stringArr[i]) != ""){
                                    etArr[i].setText(doc.get(stringArr[i]).toString());
                                }
                                else{etArr[i].setText("N/A");}
                            }

                            dayData = (ArrayList<HashMap<String, Object>>) doc.get("days");
                            MyProfileAdapter adapter = new MyProfileAdapter(dayData);
                            recyclerView.setAdapter(adapter);

                        }

                    }
                });
    }
}
