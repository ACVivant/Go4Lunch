package com.vivant.annecharlotte.go4lunch.Firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Models.Restaurant;

import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 09/02/2019.
 */
public class RestaurantHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String restoId, String restoName, int distance) {
        Restaurant restaurantToCreate = new Restaurant(restoId, restoName, distance);
        return RestaurantHelper.getRestaurantsCollection().document(restoId).set(restaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restoId){
        return RestaurantHelper.getRestaurantsCollection().document(restoId).get();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateRestoName(String restoName, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("restoname", restoName);
    }

    // --- UPDATE TODAY'S USERS---
    public static Task<Void> updateUsersToday(List<String> usersToday, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("usersToday", usersToday);
    }

    // -- GET ALL NEARBY RESTAURANT --
    public static Query getAllNearbyRestaurants(){
        return RestaurantHelper.getRestaurantsCollection().orderBy("distance", Query.Direction.ASCENDING);
    }

}