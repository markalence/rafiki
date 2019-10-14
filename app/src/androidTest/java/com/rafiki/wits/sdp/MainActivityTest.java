package com.rafiki.wits.sdp;

import android.view.View;

import androidx.test.rule.ActivityTestRule;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class MainActivityTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<MainActivity>(MainActivity.class);

    private MainActivity mActivity = null;


    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();

    }

    @Test
    public void testLaunch(){

        View view = mActivity.findViewById(R.id.main_view);

        assertNotNull(view);

    }


    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void onCreate() {
    }

    @Test
    public void setRecyclerView() {

    }

    @Test
    public void makeNavLayout() {

    }

    @Test
    public void onBackPressed() {
    }

    @Test
    public void onNavigationItemSelected() {
    }


}
