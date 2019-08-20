package com.example.mark.ms;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;


public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private EditText studentNumber;
    private EditText password;
    private EditText confirmPassword;
    private Button registerButton;
    private FirebaseFirestore db;


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
                                        Toast.makeText(getBaseContext(),"Awe", Toast.LENGTH_SHORT);
                                    }
                                    else{
                                        System.out.println(task.getException().toString());
                                        Toast.makeText(getBaseContext(), "Unlux", Toast.LENGTH_SHORT);
                                    }
                                }
                            });
                }
            }
        });
    }

    private boolean inputIsValid() {

        if (studentNumber.getText().toString().length() == 7) {
            if (password.getText().toString().equals(confirmPassword.getText().toString())) {
                if (password.getText().toString().length() >= 6) {
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
        db.collection("students").document(studentNumber.getText().toString())
                .set(userInfo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Toast.makeText(getBaseContext(), "Success", Toast.LENGTH_SHORT);
                        }
                    }
                });
    }
}
