package com.example.mqtt_t1000_hivemq;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class PublishButton {


    @Rule
    public ActivityTestRule<MainActivity> activityRule
            = new ActivityTestRule<>(MainActivity.class);


    @Test
    public void clickBtn() throws InterruptedException {
        onView(withId(R.id.btn_connect)).perform(click());
        onView(withId(R.id.btn_subscribe)).perform(click());
        onView(withId(R.id.btn_subscribe)).perform(click());
        onView(withId(R.id.et_publish)).perform(typeText("Testing publish"), closeSoftKeyboard());
        onView(withId(R.id.btn_publish)).perform(click());
        TimeUnit.SECONDS.sleep(5);
    }


}