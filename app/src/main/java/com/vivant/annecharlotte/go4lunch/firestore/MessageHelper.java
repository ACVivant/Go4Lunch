package com.vivant.annecharlotte.go4lunch.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.Query;

/**
 * Methods related to Message Collection in Firebase
 */
public class MessageHelper {

    private static final String COLLECTION_NAME = "messages";

    // --- GET ---
    public static Query getAllTodayMessageForChat(String chat, String today){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .whereEqualTo("shortDate", today)
                .limit(50);
    }

    public static Query getAllMessageForChat(String chat){
        return ChatHelper.getChatCollection()
                .document(chat)
                .collection(COLLECTION_NAME)
                .orderBy("dateCreated")
                .limit(50);
    }

    // --- CREATE ---

    public static Task<DocumentReference> createMessageForChat(String textMessage, String chat, User userSender, String shortDate){
        Message message = new Message(textMessage, userSender, shortDate);
        return ChatHelper.getChatCollection().document(chat).collection(COLLECTION_NAME).add(message);
    }

    public static Task<DocumentReference> createMessageWithImageForChat(String urlImage, String textMessage, String chat, User userSender, String shortDate){
        Message message = new Message(textMessage, urlImage, userSender, shortDate);
        return ChatHelper.getChatCollection().document(chat).collection(COLLECTION_NAME).add(message);
    }
}
