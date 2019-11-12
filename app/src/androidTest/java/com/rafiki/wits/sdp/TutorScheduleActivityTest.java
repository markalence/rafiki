package com.rafiki.wits.sdp;

import android.util.Log;
import android.view.View;

import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import com.google.firebase.Timestamp;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import static org.junit.Assert.*;

public class TutorScheduleActivityTest {
    @Before
    public void setUp() throws Exception {
        LoginActivity.upcomingTuts = new ArrayList<>();
        HashMap<String,Object> map = new HashMap<>();
        map.put("startTime", new Timestamp(new Date()));
        map.put("endTime", new Timestamp(new Date()));
        map.put("courseCode","coms3000");
        LoginActivity.upcomingTuts.add(map);
        mActivity = mActivityTestRule.getActivity();
    }

    @Rule
    public ActivityTestRule<TutorScheduleActivity> mActivityTestRule = new ActivityTestRule<>(TutorScheduleActivity.class);
    private TutorScheduleActivity mActivity = null;



    @Test
    @UiThreadTest
    public void onCreate(){
        assertNotNull(mActivity.swipeController);
        assertTrue(mActivity.recyclerViewInit());
        assertTrue(mActivity.toolbarInit());
        assert(TutorScheduleActivity.sessionAdapter.mDataset.size() == 1);
        assertTrue(TutorScheduleActivity.sessionAdapter.deleteSessions());
        View view = mActivity.getLayoutInflater().inflate(R.layout.session_item,null);
        TutorScheduleAdapter.ViewHolder vh = new TutorScheduleAdapter.ViewHolder(view);
        TutorScheduleActivity.sessionAdapter.onBindViewHolder(vh,0);
        assertTrue(TutorScheduleActivity.sessionAdapter.bound);
        assertTrue(TutorScheduleActivity.sessionAdapter.showBackOfDrawable(vh,0));
        assertTrue(TutorScheduleActivity.sessionAdapter.showFrontOfDrawable(vh,0,"12/10"));
        System.out.println(LoginActivity.upcomingTuts.toString() + " TUTS ");
        System.out.println(vh.toString() + " ???");
        assertTrue(mActivity.swipeController.handleSwipe(TutorScheduleActivity.sessionAdapter.holders.get(0), ItemTouchHelper.LEFT));
        assertTrue(mActivity.swipeController.handleSwipe(vh, ItemTouchHelper.RIGHT));
        assertTrue(mActivity.swipeController.leftSwiped);
        assertTrue(mActivity.swipeController.rightSwiped);
//        assertTrue(mActivity.swipeController.editTabDrawn);
        assertTrue(mActivity.swipeController.nothingSelected("191015:14:45",0,"191015:14:45","endTime"));
        assertTrue(mActivity.swipeController.itemSelected("191015:14:45","191015:14:45",""));
    }


}