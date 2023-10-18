package com.example.messenger

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith


/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class CheckFragmentNavigation {
    @Rule
    @JvmField
    var activityRule = ActivityScenarioRule(
        MainActivity::class.java
    )

    @Test
    fun testNavigationToChatsFragmentFromSignInFragment() {
        var myCustomIdlingResource: MyCustomIdlingResource? = null
        activityRule.scenario.onActivity {
            myCustomIdlingResource = MyCustomIdlingResource(it)
            IdlingRegistry.getInstance().register(myCustomIdlingResource)
        }

        onView(withId(R.id.email_sign_in)).perform(typeText("test@gmail.com"))
        onView(withId(R.id.password_sign_in)).perform(typeText("Dasha2004"))
        onView(withId(R.id.login_button)).perform(click())
        myCustomIdlingResource?.setLoginPerformed(true)
        onView(withId(R.id.fab_add_friend)).check(matches(isDisplayed()))
    }
}