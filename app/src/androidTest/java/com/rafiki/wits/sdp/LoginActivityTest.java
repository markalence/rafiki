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
    }

    @Test
    public void onCreate() {
        FirebaseFirestore db = mActivity.db;
        assertNotNull(db);
    }

    @Test
    @UiThreadTest
    public void directUser() {
        JSONObject j = new JSONObject();
        try {
            j.put("deviceToken", "123");
            j.put("studentNumber", "0000000");
            j.put("password", "test123");
            assertTrue(mActivity.directUser(j.toString()));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        assertFalse(mActivity.directUser("empty"));
    }

//    @Test
//    public void progressBarInit(){
//        assertTrue(mActivity.progressBarInit());
//    }


}