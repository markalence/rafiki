package com.rafiki.wits.sdp;

import android.view.View;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;


    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
        LoginActivity.studentNum = "0000000";
        LoginActivity.courseCodes = new ArrayList<>();

    }

    @Test
    @UiThreadTest
    public void testLaunch(){

        View view = mActivity.findViewById(R.id.main_view);
        assertNotNull(view);
        SubscriptionAdder sa = new SubscriptionAdder(mActivity.context,mActivity.getLayoutInflater(),mActivity.getWindowManager());
        sa.sa = new SubscriptionAdapter(mActivity.getApplicationContext(),new ArrayList<String>());
        assertTrue(sa.setRecyclerView());
        assertTrue(sa.created);
        assertNotNull(sa.cancel);
        sa.addCourse();
        sa.addStudentToCourse();
        mActivity.onBackPressed();
        mActivity.onBackPressed();

    }

//
//    @After
//    public void tearDown() throws Exception {
//    }
//
//    @Test
//    public void onCreate() {
//    }
//
//    @Test
//    public void setRecyclerView() {
//
//    }
//
//    @Test
//    public void makeNavLayout() {
//
//    }
//
//    @Test
//    public void onBackPressed() {
//    }
//
//    @Test
//    public void onNavigationItemSelected() {
//    }


}
