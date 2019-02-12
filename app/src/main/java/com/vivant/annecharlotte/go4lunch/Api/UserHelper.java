package com.vivant.annecharlotte.go4lunch.Api;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Models.User;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class UserHelper {
    private static final String COLLECTION_NAME = "users";
    private static final String TAG = "USERHELPER";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String userEmail,String urlPicture) {
        List<String> restoLikeTest=new ArrayList<>();
        restoLikeTest.add("restoLikeTest");
        User userToCreate = new User(uid, username, userEmail, urlPicture, "restoTodayTest", restoLikeTest);
        Log.d(TAG, "createUser: ");
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    // --- UPDATE TODAY'S RESTO---
    public static Task<Void> updateTodayResto(String restoToday, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restoToday", restoToday);
    }

    // --- UPDATE LIKED RESTO---
    public static Task<Void> updateLikedResto(List<String> restoLike, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restoLike", restoLike);
    }

    // --- DELETE ---
    public static Task<Void> deleteUser(String uid) {
        return UserHelper.getUsersCollection().document(uid).delete();
    }

    // -- GET ALL USERS --
    public static Query getAllUsers(String user){
        return UserHelper.getUsersCollection().orderBy("restoToday", Query.Direction.DESCENDING);
    }
}