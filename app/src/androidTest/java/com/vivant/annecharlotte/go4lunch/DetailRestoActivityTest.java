package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.authentification.AuthenticationActivity;

import org.junit.Rule;
import org.junit.Test;

import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

/**
 * Created by Anne-Charlotte Vivant on 07/03/2019.
 */
public class DetailRestoActivityTest {
    @Rule
    public ActivityTestRule<DetailRestoActivity> mDetailRestoActivityActivityTestRule = new ActivityTestRule<>(DetailRestoActivity.class);

    @Test
    public void listGoesOverTheFold() {
        onView(withId(R.id.photo_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.details_resto_container)).check(matches(isDisplayed()));
        onView(withId(R.id.name_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.address_detail)).check(matches(isDisplayed()));
        onView(withId(R.id.phone_detail_button)).check(matches(isDisplayed()));
        onView(withId(R.id.like_detail_button)).check(matches(isDisplayed()));
        onView(withId(R.id.website_detail_button)).check(matches(isDisplayed()));
        onView(withId(R.id.fragment_workmates_detailresto_recyclerview)).check(matches(isDisplayed()));

        onView(withText(mDetailRestoActivityActivityTestRule.getActivity().getResources().getString(R.string.call))).check(matches(isDisplayed()));
        onView(withText(mDetailRestoActivityActivityTestRule.getActivity().getResources().getString(R.string.website))).check(matches(isDisplayed()));
        onView(withText(mDetailRestoActivityActivityTestRule.getActivity().getResources().getString(R.string.like))).check(matches(isDisplayed()));
    }
}
