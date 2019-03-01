package com.vivant.annecharlotte.go4lunch;

import android.content.Intent;

import com.vivant.annecharlotte.go4lunch.chat.ChatActivity;
import com.vivant.annecharlotte.go4lunch.authentification.ProfileActivity;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.assertEquals;
import static org.robolectric.Shadows.shadowOf;


/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */
@RunWith(RobolectricTestRunner.class)
public class LunchActivityRobotTest {
   /* private LunchActivity activity;

    @Before
    public void setUp() {
        activity = Robolectric.buildActivity(LunchActivity.class)
                .create()
                .resume()
                .get();
    }

    @Test
    public void when_NavigationItemSettingsClicked_then_StartSettingsActivity() {
        //User clicks on Settings in navigation menu
        shadowOf(activity).clickMenuItem(R.id.nav_settings);
        Intent startedIntent = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Intent expectedIntent = new Intent(activity, SettingsActivity.class);
        assertEquals(expectedIntent.getComponent(), startedIntent.getComponent());
    }

    @Test
    public void when_NavigationItemProfileClicked_then_StartProfileActivity() {
        //User clicks on Settings in navigation menu
        shadowOf(activity).clickMenuItem(R.id.nav_logout);
        Intent startedIntent = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Intent expectedIntent = new Intent(activity, ProfileActivity.class);
        assertEquals(expectedIntent.getComponent(), startedIntent.getComponent());
    }

    @Test
    public void when_NavigationItemMyLunchClicked_then_StartDetailActivity() {
        //User clicks on Settings in navigation menu
        shadowOf(activity).clickMenuItem(R.id.nav_mylunch);
        Intent startedIntent = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Intent expectedIntent = new Intent(activity, DetailRestoActivity.class);
        assertEquals(expectedIntent.getComponent(), startedIntent.getComponent());
    }

    @Test
    public void when_NavigationItemChatClicked_then_StartChatActivity() {
        //User clicks on Settings in navigation menu
        shadowOf(activity).clickMenuItem(R.id.nav_chat);
        Intent startedIntent = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        Intent expectedIntent = new Intent(activity, ChatActivity.class);
        assertEquals(expectedIntent.getComponent(), startedIntent.getComponent());
    }*/
}

