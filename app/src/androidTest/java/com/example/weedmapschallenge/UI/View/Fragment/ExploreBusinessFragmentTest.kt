package com.example.weedmapschallenge.UI.View.Fragment

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.rule.ActivityTestRule
import com.example.weedmapschallenge.MainActivity
import com.example.weedmapschallenge.R
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test

class ExploreBusinessFragmentTest{

    @get:Rule
    public var mActivityRule: ActivityTestRule<MainActivity> = ActivityTestRule(MainActivity::class.java)

    @Test
    fun exploreBusiness_withText(){
        onView(withId(R.id.explore_text_view)).perform(typeText("ab"))
        Thread.sleep(5000)
        onView(withId(R.id.explore_text_view)).perform(typeText(""))
        Thread.sleep(5000)
        onView(withId(R.id.explore_text_view)).perform(typeText(" "))
        Thread.sleep(5000)
        onView(withId(R.id.explore_text_view)).perform(typeText("     "))
        Thread.sleep(5000)
        onView(withId(R.id.explore_text_view)).perform(typeText("******"))
        Thread.sleep(5000)
        onView(withId(R.id.explore_text_view)).perform(typeText("zzzzzzzzzz"))
    }
}