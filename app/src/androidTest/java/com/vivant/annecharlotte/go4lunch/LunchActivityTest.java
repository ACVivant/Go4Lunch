package com.vivant.annecharlotte.go4lunch;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

/**
 * Created by Anne-Charlotte Vivant on 07/03/2019.
 */
@RunWith(AndroidJUnit4.class)
public class LunchActivityTest {
    @Rule
    public ActivityTestRule<LunchActivity> mLunchActivityActivityTestRule = new ActivityTestRule<>(LunchActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withSubstring(mLunchActivityActivityTestRule.getActivity().getResources().getString(R.string.TB_title))).check(matches(isDisplayed()));
        onView(withId(R.id.navigation)).check(matches(isDisplayed()));
        onView(withId(R.id.ic_gps)).check(matches(isDisplayed()));
    }

}
