package com.rafiki.wits.sdp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;

public class TutorScheduleActivity extends AppCompatActivity {

    static ArrayList<HashMap<String, Object>> sessionItems = new ArrayList<>();
    static RecyclerView recyclerView;
    public static TutorScheduleAdapter sessionAdapter;
    static ImageButton deleteButton;
    static Toolbar toolbar;
    static TextView toolbarTitle;
    SessionSwipeController swipeController;
    static View sessionView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_session);
        swipeController =  new SessionSwipeController(this,getLayoutInflater());
        sessionItems.clear();
        sessionView = findViewById(android.R.id.content);
        deleteButton = findViewById(R.id.deleteButton);
        deleteButton.setVisibility(View.INVISIBLE);
        toolbarTitle = findViewById(R.id.toolbarTitle);
        toolbarInit();
        recyclerViewInit();
    }


    public boolean toolbarInit() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(TutorScheduleActivity.this, TutorHomeActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    public boolean recyclerViewInit() {
        if(LoginActivity.upcomingTuts == null){
            LoginActivity.upcomingTuts = new ArrayList<>();
        }
        recyclerView = findViewById(R.id.sessionSheet);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        sessionAdapter = new TutorScheduleAdapter(this, LoginActivity.upcomingTuts);
        recyclerView.setAdapter(sessionAdapter);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeController);
        itemTouchHelper.attachToRecyclerView(recyclerView);
        return true;
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
