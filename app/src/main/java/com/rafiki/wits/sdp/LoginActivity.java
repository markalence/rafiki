package com.rafiki.wits.sdp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.github.ybq.android.spinkit.style.WanderingCubes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class LoginActivity extends AppCompatActivity {

    public static String studentNum;
    public static ArrayList<String> studentCourses = new ArrayList<>();
    public static String password;
    public static String studentDocId;
    public static ArrayList<HashMap<String, Object>> interactionList;
    public static ArrayList<HashMap<String, Object>> upcomingTuts;
    public static ArrayList<String> courseCodes;
    private boolean tutsLoaded = false;
    private boolean interactionListLoaded = false;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;
    private Resources r;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;
    private static int status = 0;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        interactionList = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setPersistenceEnabled(false)
                .build();
        db.setFirestoreSettings(settings);
        firebaseAuth = FirebaseAuth.getInstance();
        r = getBaseContext().getResources();
        setContentView(R.layout.login_loading);
        progressBar = findViewById(R.id.progress);
        WanderingCubes wc = new WanderingCubes();
        progressBar.setIndeterminateDrawable(wc);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        String userData = mSharedPreferences.getString(r.getString(R.string.USER_DATA), r.getString(R.string.EMPTY));
        System.out.println(userData);

        if (userData.equals(r.getString(R.string.EMPTY))) {
            getLoginInfo();

        } else {

            try {
                JSONObject j = new JSONObject(userData);
                studentNum = j.getString(r.getString(R.string.STUDENT_NUMBER));
                password = j.getString(r.getString(R.string.PASSWORD));
                getData();
            } catch (JSONException e) {
                e.printStackTrace();
                getLoginInfo();
            }
        }

    }

    public void getLoginInfo() {
        setContentView(R.layout.activity_login);
        Button button = (Button) findViewById(R.id.loginbutton);
        Button registerButton = findViewById(R.id.register);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText usernameText = findViewById(R.id.username);
                final EditText passwordText = findViewById(R.id.password);
                studentNum = (usernameText.getText()).toString();
                password = passwordText.getText().toString();
                setContentView(R.layout.login_loading);

                firebaseAuth.signInWithEmailAndPassword(studentNum + r.getString(R.string.EMAIL_POSTFIX), password)
                        .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    status = 1;
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    JSONObject j = new JSONObject();
                                    try {
                                        j.put(r.getString(R.string.STUDENT_NUMBER), studentNum);
                                        j.put(r.getString(R.string.PASSWORD), password);
                                        mEditor.putString(r.getString(R.string.USER_DATA), j.toString());
                                        mEditor.commit();

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                                if (!task.isSuccessful()) {
                                    Toast.makeText(getBaseContext(), r.getString(R.string.INVALID_LOGIN), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        });
    }


    public void updateToken() {
        db.collection(r.getString(R.string.STUDENTS))
                .document(studentNum)
                .update(r.getString(R.string.DEVICE_TOKEN), mSharedPreferences.getString(r.getString(R.string.DEVICE_TOKEN), null))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                        }
                    }
                });
    }

    public void getStudentCourses() {
        db.collection(r.getString(R.string.STUDENTS))
                .document(studentNum)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().getData() == null) {
                                studentCourses = new ArrayList<>();
                            } else if (task.getResult().getData().get("courses") == null) {
                                studentCourses = new ArrayList<>();
                            } else {
                                studentCourses = (ArrayList<String>) task.getResult().getData();
                            }
                        }
                    }
                });
    }

    public void getAnsweredQuestions() {
        db.collection("answeredquestions")
                .orderBy(r.getString(R.string.DATE), Query.Direction.DESCENDING)
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                interactionList.add((HashMap<String, Object>) doc.getData());
                            }
                            getCourseList();

                        } else {
                            Toast.makeText(getBaseContext(), r.getString(R.string.NETWORK_ERROR), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    public void getSchedule() {
        db.collection("schedule")
                .orderBy(r.getString(R.string.DATE), Query.Direction.ASCENDING)
                .get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot doc : task.getResult()) {
                        HashMap<String, Object> info = (HashMap<String, Object>) doc.getData();
                        upcomingTuts.add(info);
                    }
                    firebaseAuth.signInWithEmailAndPassword(studentNum + r.getString(R.string.EMAIL_POSTFIX), password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);
                            }
                            if (!task.isSuccessful()) {
                                System.out.println(r.getString(R.string.INVALID_LOGIN));
                            }
                        }
                    });
                } else {
                    Toast.makeText(getBaseContext(), r.getString(R.string.NETWORK_ERROR), Toast.LENGTH_SHORT).show();
                    System.out.println(task.getException());
                }
            }
        });
    }

    public void getCourseList() {
        db.collection(r.getString(R.string.COURSES))
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                courseCodes.add((String) doc.getData().get("courseCode"));
                                System.out.println(doc.getData());
                            }
                        }
                    }
                });
    }

    public boolean getData() {

        interactionList = new ArrayList<>();
        upcomingTuts = new ArrayList<>();
        courseCodes = new ArrayList<>();

        updateToken();
        getStudentCourses();
        getAnsweredQuestions();
        getCourseList();
        getSchedule();


return true;

    }

}
