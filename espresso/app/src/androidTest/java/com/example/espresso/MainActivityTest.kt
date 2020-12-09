package com.example.espresso

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ActivityTestRule
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @Rule
    @JvmField
    val mActivityTestRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun helloWorldText() {
        onView(withId(R.id.text_hello_world))
            .check(matches(withText("Hello World!")))
    }

    @Test
    fun changeText(){
        // Click button
        onView(withId(R.id.button_change_message)).perform(click())
        // Text will change
        onView(withId(R.id.text_hello_world)).check(matches(withText("Espresso")))
    }

    @Test
    fun setMessage() {
        // set message and close keyboard
        onView(withId(R.id.et_message)).perform(typeText("Android"), closeSoftKeyboard())
        // change text
        onView(withId(R.id.button_change_message)).perform(click())
        onView(withId(R.id.text_hello_world)).check(matches(withText("Android")))
    }
}