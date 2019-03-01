package com.vivant.annecharlotte.go4lunch;

import android.content.Context;
import com.vivant.annecharlotte.go4lunch.notifications.CreateNotifMessage;
import androidx.test.core.app.ApplicationProvider;
import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */

/*java.lang.IllegalStateException: No instrumentation registered! Must run under a registering instrumentation.

        at androidx.test.platform.app.InstrumentationRegistry.getInstrumentation(InstrumentationRegistry.java:45)
        at androidx.test.core.app.ApplicationProvider.getApplicationContext(ApplicationProvider.java:41)
        at com.vivant.annecharlotte.go4lunch.CreateNotifMessageTest.<init>(CreateNotifMessageTest.java:14)
        at sun.reflect.NativeConstructorAccessorImpl.newInstance0(Native Method)
        at sun.reflect.NativeConstructorAccessorImpl.newInstance(NativeConstructorAccessorImpl.java:62)
        at sun.reflect.DelegatingConstructorAccessorImpl.newInstance(DelegatingConstructorAccessorImpl.java:45)
        at java.lang.reflect.Constructor.newInstance(Constructor.java:423)
        at org.junit.runners.BlockJUnit4ClassRunner.createTest(BlockJUnit4ClassRunner.java:217)
        at org.junit.runners.BlockJUnit4ClassRunner$1.runReflectiveCall(BlockJUnit4ClassRunner.java:266)
        at org.junit.internal.runners.model.ReflectiveCallable.run(ReflectiveCallable.java:12)
        at org.junit.runners.BlockJUnit4ClassRunner.methodBlock(BlockJUnit4ClassRunner.java:263)
        at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:78)
        at org.junit.runners.BlockJUnit4ClassRunner.runChild(BlockJUnit4ClassRunner.java:57)
        at org.junit.runners.ParentRunner$3.run(ParentRunner.java:290)
        at org.junit.runners.ParentRunner$1.schedule(ParentRunner.java:71)
        at org.junit.runners.ParentRunner.runChildren(ParentRunner.java:288)
        at org.junit.runners.ParentRunner.access$000(ParentRunner.java:58)
        at org.junit.runners.ParentRunner$2.evaluate(ParentRunner.java:268)
        at org.junit.runners.ParentRunner.run(ParentRunner.java:363)
        at org.junit.runner.JUnitCore.run(JUnitCore.java:137)
        at com.intellij.junit4.JUnit4IdeaTestRunner.startRunnerWithArgs(JUnit4IdeaTestRunner.java:68)
        at com.intellij.rt.execution.junit.IdeaTestRunner$Repeater.startRunnerWithArgs(IdeaTestRunner.java:47)
        at com.intellij.rt.execution.junit.JUnitStarter.prepareStreamsAndStart(JUnitStarter.java:242)
        at com.intellij.rt.execution.junit.JUnitStarter.main(JUnitStarter.java:70)*/

public class CreateNotifMessageTest {
    private Context context = ApplicationProvider.getApplicationContext();

   /* @Test
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
    }*/
}


