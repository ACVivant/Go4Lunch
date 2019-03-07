package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.authentification.AuthenticationActivity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Anne-Charlotte Vivant on 07/03/2019.
 */
@RunWith(AndroidJUnit4.class)
public class AuthenticationActivityTest {
    @Rule
    public ActivityTestRule<AuthenticationActivity> mAuthenticationActivityActivityTestRule = new ActivityTestRule<>(AuthenticationActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withText("Go4Lunch")).check(matches(isDisplayed()));
        onView(withText(mAuthenticationActivityActivityTestRule.getActivity().getResources().getString(R.string.main_activity_description))).check(matches(isDisplayed()));
        onView(withId(R.id.logo_main)).check(matches(isDisplayed()));
    }
}
