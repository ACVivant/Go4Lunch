package com.vivant.annecharlotte.go4lunch.Firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Models.Restaurant;
import com.vivant.annecharlotte.go4lunch.Models.User;

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
    public static Task<Void> createRestaurant(String restoId, String restoName) {
        Restaurant restaurantToCreate = new Restaurant(restoId, restoName);
        return RestaurantHelper.getRestaurantsCollection().document(restoId).set(restaurantToCreate);
    }

    // --- CREATE ---
    public static Task<Void> createDetailRestaurant(String restoId, String restoName, String urlPhoto, String address, String phone, String website, double rate, double lat, double lng, String hoursMonday, String hoursTuesday, String hoursWednesday, String hoursThursday, String hoursFriday, String hoursSaturday, String  hoursSunday) {
        Restaurant restaurantToCreate = new Restaurant(restoId, restoName, urlPhoto, address, phone, website, rate, lat, lng, hoursMonday, hoursTuesday, hoursWednesday, hoursThursday, hoursFriday, hoursSaturday, hoursSunday);
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