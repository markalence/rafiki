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
    public static String firstName;
    public static String lastName;
    public static String password;
    public static String grade;
    public static ArrayList<HashMap<String, String>> userDays;
    public static ArrayList<HashMap<String, String>> userContacts;
    public static ArrayList<HashMap<String, Object>> recordSheet;
    public static ArrayList<HashMap<String, Object>> upcomingSessions;
    private boolean sessionsLoaded = false;
    private boolean recordSheetLoaded = false;
    private boolean daysLoaded = false;
    private SharedPreferences mSharedPreferences;
    private SharedPreferences.Editor mEditor;
    private Resources r;
    private FirebaseAuth firebaseAuth;
    private ProgressBar progressBar;

    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        recordSheet = new ArrayList<>();
        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
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
        String userData = mSharedPreferences.getString("userData", "empty");
        System.out.println(userData);

        if (userData.equals("empty")) {
            getLoginInfo();

        } else {

            try {
                JSONObject j = new JSONObject(userData);
                studentNum = j.getString("studentNumber");
                password = j.getString("password");
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
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
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

                firebaseAuth.signInWithEmailAndPassword(studentNum + "@students.wits.ac.za", password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            JSONObject j = new JSONObject();
                            try {
                                j.put("studentNumber", studentNum);
                                j.put("password", password);
                                j.put(r.getString(R.string.GRADE), grade);
                                mEditor.putString("userData", j.toString());
                                mEditor.commit();

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                        if (!task.isSuccessful()) {
                            System.out.println("Invalid login name or password");
                        }
                    }
                });
            }
        });
    }

    public void getData() {

        recordSheet = new ArrayList<>();
        upcomingSessions = new ArrayList<>();
        userDays = new ArrayList<>();

        db.collection(r.getString(R.string.STUDENTS))
                .document(studentNum)
                .update("deviceToken",mSharedPreferences.getString("deviceToken",null));

        db.collection(r.getString(R.string.RECORDSHEETS))
                .whereEqualTo(r.getString(R.string.USERNAME), studentNum)
                .orderBy(r.getString(R.string.DATE), Query.Direction.DESCENDING)
                .get(Source.SERVER)
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (DocumentSnapshot doc : task.getResult()) {
                                recordSheet.add((HashMap<String, Object>) doc.getData());
                            }
                            recordSheetLoaded = true;
                            if (sessionsLoaded && recordSheetLoaded && daysLoaded) {
                                finish();
                                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                startActivity(intent);

                            }
                        } else {
                            Log.d("BAD", task.getException().toString());
                            Toast.makeText(getBaseContext(), "Couldn't connect", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

        db.collection("courses")
                .document("coms3005")
                .collection("tutorials")
                .orderBy(r.getString(R.string.DATE), Query.Direction.ASCENDING)
                .get(Source.SERVER).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {

                    for (DocumentSnapshot doc : task.getResult()) {
                        HashMap<String, Object> info = (HashMap<String, Object>) doc.getData();
                        upcomingSessions.add(info);
                    }
                    sessionsLoaded = true;

                        firebaseAuth.signInWithEmailAndPassword(studentNum + "@students.wits.ac.za", password).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser user = firebaseAuth.getCurrentUser();
                                    Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                                    startActivity(intent);
                                }
                                if (!task.isSuccessful()) {
                                    System.out.println("Invalid login name or password");
                                }
                            }
                        });


                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        finish();
                        startActivity(intent);
                } else {
                    Toast.makeText(getBaseContext(), "Could not connect to database", Toast.LENGTH_SHORT).show();
                    System.out.println(task.getException());
                }
            }
        });

    }

}
