package com.rafiki.wits.sdp;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class TutorHomeActivityTest {
    @Rule
    public ActivityTestRule<TutorHomeActivity> mActivityTestRule = new ActivityTestRule<>(TutorHomeActivity.class);

    private TutorHomeActivity mActivity = null;


    @Before
    public void setUp() throws Exception {

        mActivity = mActivityTestRule.getActivity();
        LoginActivity.studentNum = "0000000";
        LoginActivity.studentCourses = new ArrayList<>();

    }

    @Test
    @UiThreadTest
    public void testLaunch(){
        assertNotNull(mActivity.pqa);
        assertTrue(mActivity.setRecyclerView());
        assertNotNull(mActivity.pqa.confirm);
        assertTrue(mActivity.makeNavLayout());
        mActivity.getQuestions();
        mActivity.onBackPressed();
        mActivity.onBackPressed();
    }

}