package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.authentification.AuthenticationActivity;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.Espresso.openContextualActionModeOverflowMenu;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Anne-Charlotte Vivant on 07/03/2019.
 */
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
