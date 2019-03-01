package com.vivant.annecharlotte.go4lunch;

import android.content.Context;

import com.vivant.annecharlotte.go4lunch.notifications.CreateNotifMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;
import org.robolectric.annotation.Config;

import static org.junit.Assert.assertEquals;

/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */
@RunWith(RobolectricTestRunner.class)
@Config(manifest= Config.NONE)
public class CreateNotifMessageTest {
    private Context context = RuntimeEnvironment.application.getApplicationContext();

    @Test
    public void message_notification_creation_with_comma() {
        CreateNotifMessage message = new CreateNotifMessage();
        String expectedMessage = context.getResources().getString(R.string.notif_message1)+"Marmaris rue de la digestion"+"\n" + context.getResources().getString(R.string.notif_message2)+" Jean, Paul";
        assertEquals(expectedMessage, message.create(context, R.string.notif_message1, "Marmaris", "rue de la digestion", R.string.notif_message2, "Jean, Paul, "));
    }

    @Test
    public void message_notification_creation_without_comma() {
        CreateNotifMessage message = new CreateNotifMessage();
        String expectedMessage = context.getResources().getString(R.string.notif_message1)+"Marmaris rue de la digestion"+"\n" + context.getResources().getString(R.string.notif_message2)+" Jean, Paul";
        assertEquals(expectedMessage, message.create(context, R.string.notif_message1, "Marmaris", "rue de la digestion", R.string.notif_message2, "Jean, Paul"));
    }
}
