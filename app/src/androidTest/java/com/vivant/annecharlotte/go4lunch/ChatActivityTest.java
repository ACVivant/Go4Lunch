package com.vivant.annecharlotte.go4lunch;

import com.vivant.annecharlotte.go4lunch.authentification.ProfileActivity;
import com.vivant.annecharlotte.go4lunch.chat.ChatActivity;

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
public class ChatActivityTest {
    @Rule
    public ActivityTestRule<ChatActivity> mChatActivityActivityTestRule = new ActivityTestRule<>(ChatActivity.class);

    // Test tab layout
    @Test
    public void listGoesOverTheFold() {
        onView(withText(mChatActivityActivityTestRule.getActivity().getResources().getString(R.string.toolbar_title_chat_activity))).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_restaurant_chat_button)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_photo_chat_button)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_recycler_view_container)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_image_chosen_preview)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_add_message_container)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_add_file_button)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_message_edit_text)).check(matches(isDisplayed()));
        onView(withId(R.id.activity_user_chat_send_button)).check(matches(isDisplayed()));
    }
}
