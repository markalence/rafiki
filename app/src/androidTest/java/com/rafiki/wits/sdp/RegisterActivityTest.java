
package com.rafiki.wits.sdp;

import androidx.test.annotation.UiThreadTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

public class RegisterActivityTest {

    @Before
    @UiThreadTest
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();

    }

    @Rule
    public ActivityTestRule<RegisterActivity> mActivityTestRule = new ActivityTestRule<>(RegisterActivity.class);
    private RegisterActivity mActivity = null;

    @Test
    @UiThreadTest
    public void test(){
        assertNotNull(mActivity.radioButton);
        assertFalse(mActivity.inputIsValid());
        assertFalse(mActivity.info);
    }

}