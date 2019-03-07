package com.vivant.annecharlotte.go4lunch;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withSubstring;

/**
 * Created by Anne-Charlotte Vivant on 07/03/2019.
 */
public class SettingsActivityTest {
    @Rule
    public ActivityTestRule<SettingsActivity> mSettingsActivityActivityTestRule = new ActivityTestRule<>(SettingsActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withSubstring(mSettingsActivityActivityTestRule.getActivity().getResources().getString(R.string.radius_text))).check(matches(isDisplayed()));
        onView(withSubstring(mSettingsActivityActivityTestRule.getActivity().getResources().getString(R.string.type_text))).check(matches(isDisplayed()));
        onView(withSubstring(mSettingsActivityActivityTestRule.getActivity().getResources().getString(R.string.notifications_onoff))).check(matches(isDisplayed()));

        onView(withId(R.id.spinner_radius)).check(matches(isDisplayed()));
        onView(withId(R.id.spinner_type)).check(matches(isDisplayed()));
        onView(withId(R.id.switch_notifications)).check(matches(isDisplayed()));
        onView(withId(R.id.settings_search_btn)).check(matches(isDisplayed()));

    }
}
