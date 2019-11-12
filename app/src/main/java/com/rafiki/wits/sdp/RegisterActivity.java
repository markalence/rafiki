package com.rafiki.wits.sdp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText studentNumber;
    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;
    private FirebaseFirestore db;
    boolean info;
    RadioButton radioButton;
    SharedPreferences.Editor mEditor;
    SharedPreferences mSharedPreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        studentNumber = findViewById(R.id.regStudentNumber);
        password = findViewById(R.id.regPassword);
        confirmPassword = findViewById(R.id.regConfirmPassword);
        registerButton = findViewById(R.id.regButton);
        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mEditor = mSharedPreferences.edit();
        radioButton = findViewById(R.id.radioButton);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(inputIsValid()){
                    String email = studentNumber.getText().toString() + "@students.wits.ac.za";
                    firebaseAuth.createUserWithEmailAndPassword(email,password.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if(task.isSuccessful()){
                                        writeUserInfo();
                                    }
                                    else{
                                        System.out.println(task.getException().toString());
                                    }
                                }
                            });
                }
            }
        });
    }

    public boolean inputIsValid() {

        if (studentNumber.getText().toString().length() == 7) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                if (password.getText().toString().length() >= 6) {
                    JSONObject j = new JSONObject();
                    try {
                        j.put("studentNumber",studentNumber.getText().toString());
                        j.put("password",password.getText().toString());
                        mEditor.putString("userData", j.toString());
                        mEditor.commit();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    return true;
                } else {
                    Toast.makeText(this.getBaseContext(), "Password needs to be greater than six characters", Toast.LENGTH_SHORT);
                }
            } else {
                Toast.makeText(this.getBaseContext(), "Passwords don't match", Toast.LENGTH_SHORT);
            }
        } else {
            Toast.makeText(this.getBaseContext(), "Incorrect student number", Toast.LENGTH_SHORT);
        }
        return false;
    }

    private void writeUserInfo(){
        Map userInfo = new HashMap();
        userInfo.put("studentNumber",studentNumber.getText().toString());
        if(radioButton.isChecked()){
            userInfo.put("role","tutor");
        }
        else{
            userInfo.put("role","student");
        }
        db.collection("students").document(studentNumber.getText().toString())
                .set(userInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT);
                        }
                    }
                });
        info = true;
    }
}