package com.vivant.annecharlotte.go4lunch.Notifications;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.os.Build;
import android.util.Log;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois.RestaurantHelper;
import com.vivant.annecharlotte.go4lunch.Firestore.UserHelper;
import com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois.Restaurant;
import com.vivant.annecharlotte.go4lunch.Models.User;
import com.vivant.annecharlotte.go4lunch.R;
import com.vivant.annecharlotte.go4lunch.Utils.DateFormat;
import com.vivant.annecharlotte.go4lunch.authentification.MainActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import androidx.core.app.NotificationCompat;

public class NotificationsService extends FirebaseMessagingService {
    private static final String TAG = "NotificationsService";
    private final int NOTIFICATION_ID = 007;
    private final String NOTIFICATION_TAG = "FIREBASEOC";
    public static final String SHARED_PREFS = "SharedPrefsPerso";
    public static final String NOTIF_PREFS = "notifications";

    private String userName;
    private String userId;
    private String restoTodayId;
    private String restoTodayName;
    private String restoTodayAddress;
    private List<String> listUserId = new ArrayList<>();
    private String listNames="";
    private String myMessage;
    private boolean notifOk;
    private Context context;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        notifOk = sharedPreferences.getBoolean(NOTIF_PREFS, true);
        userId = UserHelper.getCurrentUserId();

        // On regarde si l'utilisateur veut recevoir les notifications
        checkIfNotifToday();
    }


    private void checkIfNotifToday() {
        DateFormat forToday = new DateFormat();
        final String today = forToday.getTodayDate();

        // On vérifie que l'utilisateur a sélecitonné un resto pour aujourd'hui
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                String myRestoToday = user.getRestoToday();
                String registeredDate = user.getRestoDate();
                if (!myRestoToday.equals("")) {
                    // On vérifie que le resto a été enregistré pour aujourd'hui
                    if (registeredDate.equals(today)) {
                        // On vérifie qu'il a souscrit à l'envoi de notifications
                        if (notifOk) {
                            Log.d(TAG, "onMessageReceived: switch " + notifOk);
                            createPersonalizedMessage();
                        }
                    }
                }
            }
        });
    }

    private void createPersonalizedMessage() {
        context = this.getApplicationContext();

        // Je récupère l'id du restoToday de l'utilisateur
        UserHelper.getUser(userId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                User user = documentSnapshot.toObject(User.class);
                userName = user.getUsername();
                restoTodayId=user.getRestoToday();

                // Je récupère le nom et l'adresse du resto correspondant
                RestaurantHelper.getRestaurant(restoTodayId).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Restaurant resto = documentSnapshot.toObject((Restaurant.class));
                        restoTodayName = resto.getRestoName();
                        restoTodayAddress = resto.getAddress();
                        // Je récupère la liste des collègues ayant choisi ce resto
                        listUserId = resto.getUsersToday();

                        // Je récupère la liste des noms des collègues
                        for (int i=0; i<listUserId.size(); i++) {
                            UserHelper.getUser(listUserId.get(i)).addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                @Override
                                public void onSuccess(DocumentSnapshot documentSnapshot) {
                                    User user = documentSnapshot.toObject(User.class);
                                    String name = user.getUsername();
                                    listNames+= name +", ";
                                    Log.d(TAG, "onSuccess: listnames " +listNames);
                                }
                            });
                        }

                        // Je crée le message
                        CreateNotifMessage message = new CreateNotifMessage();
                        myMessage = message.create(context, R.string.notif_message1, restoTodayName, restoTodayAddress, R.string.notif_message2, listNames);
/*                                    // Je crée le message
                                    myMessage = getString(R.string.notif_message1) + restoTodayName + " " + restoTodayAddress +"\n" + getString(R.string.notif_message2) + " " +listNames;
                                    Log.d(TAG, "createPersonalizedMessage: mymessage " +myMessage);

                                    if(myMessage.endsWith(", ")) myMessage = myMessage.substring(0, myMessage.length() - 2);
                                    Log.d(TAG, "createPersonalizedMessage: mymessage sans virgule " +myMessage);*/
                        sendVisualNotification(myMessage);
                    }
                });
            }
        });
    }

    private void sendVisualNotification(String messageBody) {
        // 1 - Create an Intent that will be shown when user will click on the Notification
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        // 2 - Create a Style for the Notification
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        inboxStyle.setBigContentTitle(getString(R.string.notification_title));
        inboxStyle.addLine(messageBody);

        // 3 - Create a Channel (Android 8)
        String channelId = getString(R.string.default_notification_channel_id);

        // 4 - Build a Notification object
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.baseline_local_dining_24)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(getString(R.string.notification_title))
                        .setAutoCancel(true)
                        .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                        .setContentIntent(pendingIntent)
                        .setStyle(inboxStyle);

        // 5 - Add the Notification to the Notification Manager and show it.
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // 6 - Support Version >= Android 8
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence channelName = "Message provenant de Firebase";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel mChannel = new NotificationChannel(channelId, channelName, importance);
            notificationManager.createNotificationChannel(mChannel);
        }

        // 7 - Show notification
        notificationManager.notify(NOTIFICATION_TAG, NOTIFICATION_ID, notificationBuilder.build());
    }

}
