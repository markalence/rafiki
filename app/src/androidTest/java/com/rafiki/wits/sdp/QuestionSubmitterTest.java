package com.rafiki.wits.sdp;

import static org.junit.Assert.*;


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
import java.util.HashMap;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class QuestionSubmitterTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule = new ActivityTestRule<>(MainActivity.class, true, true);
    private MainActivity
            mActivity = null;

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
    }

    @Test
    @UiThreadTest
    public void numberPickerInit(){
        QuestionSubmitter q = new QuestionSubmitter(mActivity.getApplicationContext(),mActivity.getLayoutInflater());
        assertTrue(q.numberPickerInit());
    }

}