package com.vivant.annecharlotte.go4lunch.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

/**
 * Methods related to Restaurant Collection in Firebase
 */
public class RestauranHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String restoId, String restoName, String address) {
        Restaurant restaurantToCreate = new Restaurant(restoName, address);
        return RestauranHelper.getRestaurantsCollection().document(restoId).set(restaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restoId){
        return RestauranHelper.getRestaurantsCollection().document(restoId).get();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateRestoName(String restoName, String restoId) {
        return RestauranHelper.getRestaurantsCollection().document(restoId).update("restoname", restoName);
    }

    // --- UPDATE TODAY'S USERS---
    public static Task<Void> updateClientsTodayList(List<String> clientsTodayList, String restoId) {
        return RestauranHelper.getRestaurantsCollection().document(restoId).update("clientsTodayList", clientsTodayList);
    }

}