package com.rafiki.wits.sdp;

import android.widget.ProgressBar;

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

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class LoginActivityTest {

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class, true, true);
    private LoginActivity mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        LoginActivity.studentNum = "0000000";
        LoginActivity.password = "test123";
    }

    @Test
    @UiThreadTest
    public void onCreate() {
        FirebaseFirestore db = mActivity.db;
        assertNotNull(db);
        assertNotNull(mActivity.mSharedPreferences);
        assertNotNull(mActivity.mSharedPreferences);
        mActivity.getData();
        assertTrue(mActivity.tokenUpdated);
        assertTrue(mActivity.coursesRetrieved);
        assertTrue(mActivity.scheduleRetrieved);
        assertTrue(mActivity.announcementsRetrieved);
        assertTrue(mActivity.interactionsRetrieved);
        assertNotNull(mActivity.mSharedPreferences.getString("userData","empty"));

    }


//    @Test
//    public void progressBarInit(){
//        assertTrue(mActivity.progressBarInit());
//    }


}