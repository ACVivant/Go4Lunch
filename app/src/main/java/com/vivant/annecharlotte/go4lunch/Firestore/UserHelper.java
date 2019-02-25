package com.vivant.annecharlotte.go4lunch.Firestore;

import android.util.Log;

import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Models.User;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class UserHelper {
    private static final String COLLECTION_NAME = "users";
    private static final String TAG = "USERHELPER";

    FirebaseFirestore mFirebaseFirestore;
    FirebaseAuth mFirebaseAuth;

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getUsersCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createUser(String uid, String username, String userEmail,String urlPicture) {
        User userToCreate = new User(uid, username, userEmail, urlPicture);
        Log.d(TAG, "createUser: ");
        return UserHelper.getUsersCollection().document(uid).set(userToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getUser(String uid){
        return UserHelper.getUsersCollection().document(uid).get();
    }

    // --- GET CURRENT USER ID ---
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    // --- GET CURRENT USER NAME ---
    public static String getCurrentUserName() {
        return FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
    }

    // --- GET CURRENT USER URL PICTURE ---
    public static String getCurrentUserUrlPicture() {
        return FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl().toString();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateUsername(String username, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("username", username);
    }

    // --- UPDATE TODAY'S RESTO---
    public static Task<Void> updateTodayResto(String restoToday, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restoToday", restoToday);
    }

    // --- UPDATE TODAY'S RESTO---
    public static Task<Void> updateTodayRestoName(String restoTodayName, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restoTodayName", restoTodayName);
    }

    // --- UPDATE DATE'S RESTO---
    public static Task<Void> updateRestoDate(String restoDate, String uid) {
        return UserHelper.getUsersCollection().document(uid).update("restoDate", restoDate);
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
    public static Query getAllUsers(){
        return UserHelper.getUsersCollection().orderBy("restoToday", Query.Direction.DESCENDING);
    }
}