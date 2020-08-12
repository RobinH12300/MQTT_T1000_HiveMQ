package com.example.mqtt_t1000_hivemq;

import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

public class getData {

    String receivedData;

    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);

    @Before
    public void initValidReceivedData(){
        receivedData = "123";
    }


    @Test
    public void clickBtn() throws InterruptedException {
        onView(withId(R.id.btn_connect)).perform(click());
        onView(withId(R.id.dataReceived)).check(matches(withText(receivedData)));
    }

}
