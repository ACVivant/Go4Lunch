package com.vivant.annecharlotte.go4lunch.firestore;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

/**
 * Created by Anne-Charlotte Vivant on 22/02/2019.
 */
public class ChatHelper {

    private static final String COLLECTION_NAME = "chats";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getChatCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }
}
