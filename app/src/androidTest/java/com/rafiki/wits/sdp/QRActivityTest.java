
package com.rafiki.wits.sdp;

import android.os.Build;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class QRActivityTest {
    @Before
    @UiThreadTest
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();

    }

    @Rule
    public ActivityTestRule<QRActivity> mActivityTestRule = new ActivityTestRule<>(QRActivity.class);
    private QRActivity mActivity = null;

    @Test
    @UiThreadTest
    public void test(){
        assertTrue(mActivity.setApiVersion(Build.VERSION.SDK_INT));
        System.out.println(mActivity.checkPermission() + " BOLEAN");
        assertTrue(mActivity.checkPermission());
        assertTrue(mActivity.requestPermission());
        assertTrue(mActivity.created);


    }
}