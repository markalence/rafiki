package com.rafiki.wits.sdp;

import androidx.test.annotation.UiThreadTest;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;

import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
@LargeTest

public class StudentScheduleActivityTest {

    @Rule
    public ActivityTestRule<StudentScheduleActivity> mActivityTestRule = new ActivityTestRule<StudentScheduleActivity>(StudentScheduleActivity.class, true, true);
    private StudentScheduleActivity mActivity = null;

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
        assertTrue(StudentScheduleActivity.sessionItems.size()==0);
    }


}