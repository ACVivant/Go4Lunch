package com.vivant.annecharlotte.go4lunch.notifications;

import android.content.Context;

/**
 * Created by Anne-Charlotte Vivant on 28/02/2019.
 */
public class CreateNotifMessage {

    public String create(Context context, int notif1, String resto, String address, int notif2, String names) {
        String message = context.getResources().getString(notif1) + resto + " " +address +"\n" + context.getResources().getString(notif2) + " " +names;
        if(message.endsWith(", ")) message = message.substring(0, message.length() - 2);
        return message;
    }
}


