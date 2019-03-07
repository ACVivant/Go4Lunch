package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.authentification.ProfileActivity;

import org.junit.Rule;
import org.junit.Test;

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
public class ProfileActivityTest {

    @Rule
    public ActivityTestRule<ProfileActivity> mMainActivityActivityTestRule = new ActivityTestRule<>(ProfileActivity.class);

    // Test tab layout
    @Test
    public void listGoesOverTheFold() {
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.toolbar_title_login_activity))).check(matches(isDisplayed()));
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.text_input_username))).check(matches(isDisplayed()));
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.text_view_email))).check(matches(isDisplayed()));
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.button_update_account))).check(matches(isDisplayed()));
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.button_delete_account))).check(matches(isDisplayed()));
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.button_sign_out_account))).check(matches(isDisplayed()));

    }

    @Test
    public void clickSearchButton_opensSearchActivity()throws Exception {
        onView(withId(R.id.profile_activity_button_delete)).perform(click());
        onView(withText(mMainActivityActivityTestRule.getActivity().getResources().getString(R.string.popup_message_confirmation_delete_account))).check(matches(isDisplayed()));
    }

}
