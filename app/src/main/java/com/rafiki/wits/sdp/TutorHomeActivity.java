package com.rafiki.wits.sdp;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class TutorHomeActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    static View view;
    public static ArrayList<HashMap<String, Object>> questions = new ArrayList<>();
    public ArrayList<String> courses;
    private Resources r;
    private TextView nameView;
    private boolean exit = false;
    public PendingQuestionAdapter pqa;
    private RecyclerView recyclerView;
    public FirebaseFirestore db;
    public SharedPreferences mSharedPreferences;
    public SharedPreferences.Editor mEditor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_home);
        db = FirebaseFirestore.getInstance();
        questions = new ArrayList<>();
        pqa = new PendingQuestionAdapter(TutorHomeActivity.this, getLayoutInflater(),questions);
        getQuestions();
        makeNavLayout();
        setRecyclerView();
        view = findViewById(android.R.id.content);
        r = getBaseContext().getResources();
    }

    public boolean setRecyclerView() {
        if (LoginActivity.interactionList != null) {
            recyclerView = findViewById(R.id.questionList);
            recyclerView.setLayoutManager(new LinearLayoutManager(this));
            recyclerView.setAdapter(pqa);
            return true;
        }
        return false;
    }


    public void getQuestions() {

        for (String course : LoginActivity.studentCourses) {
            db.collection("pendingquestions")
                    .whereEqualTo("courseCode", course)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                for (DocumentSnapshot doc : task.getResult()) {
                                    System.out.println(doc.getData());
                                    questions.add((HashMap<String, Object>) doc.getData());
                                    pqa.notifyDataSetChanged();

                                }
                            }
                        }
                    });
        }

    }

    public void makeNavLayout() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        ImageView header = navigationView.getHeaderView(0).findViewById(R.id.imageView);

        nameView = navigationView.getHeaderView(0).findViewById(R.id.name);
//        gradeView = navigationView.getHeaderView(0).findViewById(R.id.grade);
        nameView.setText(LoginActivity.studentNum);
        Picasso.get().load(R.mipmap.header_background).into(header);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (!exit) {
                exit = true;
                Toast.makeText(getBaseContext(), "Press back again to exit", Toast.LENGTH_SHORT).show();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 2000);
            } else {

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.sessions){
            Intent intent = new Intent(TutorHomeActivity.this,TutorScheduleActivity.class);
            startActivity(intent);
        }
        else if(id == R.id.logout){
            mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(LoginActivity.context);
            mEditor = mSharedPreferences.edit();
            mEditor.clear();
            mEditor.commit();
            Intent intent = new Intent(TutorHomeActivity.this,LoginActivity.class);
            startActivity(intent);
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}  
