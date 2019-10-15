package com.rafiki.wits.sdp;

import android.os.Bundle;

import androidx.test.annotation.UiThreadTest;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.google.firebase.firestore.FirebaseFirestore;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class TutActivityTest {

    @Rule
    public ActivityTestRule<TutActivity> mActivityTestRule = new ActivityTestRule<TutActivity>(TutActivity.class, true, true);
    private TutActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        LoginActivity.upcomingTuts = new ArrayList<>();
    }

    @Test
    @UiThreadTest
    public void toolBarInit(){
        LoginActivity.upcomingTuts = new ArrayList<>();
        assertTrue(mActivity.toolbarInit());
    }

    @Test
    @UiThreadTest
    public void recyclerViewInit(){
        LoginActivity.upcomingTuts = new ArrayList<>();
        assertTrue(mActivity.recyclerViewInit());
    }

    @Test
    @UiThreadTest
    public void onCreate(){
        assertTrue(mActivity.sessionItems.size()==0);
    }


}