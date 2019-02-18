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

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restoId){
        return RestaurantHelper.getRestaurantsCollection().document(restoId).get();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateRestoName(String restoName, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("restoname", restoName);
    }

    // --- UPDATE DETAIL TODAY'S USERS---
    public static Task<Void> updateDetailUsersToday(List<User> detailUsersToday, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("detailUsersToday", detailUsersToday);
    }

    // --- UPDATE TODAY'S USERS---
    public static Task<Void> updateUsersToday(List<String> usersToday, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("usersToday", usersToday);
    }

    // --- UPDATE TODAY'S USERS NAME---
    public static Task<Void> updateNameUsersToday(List<String> nameUsersToday, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("usersToday", nameUsersToday);
    }

    // --- ADD DETAIL TODAY'S USER---
    public static Task<DocumentReference> addDetailUserToday(User iAmUserToday, String restoId, String allUsers) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).collection(allUsers).add(iAmUserToday);
    }

    // -- GET ALL NEARBY RESTAURANT --
    public static Query getAllNearbyRestaurants(){
        return RestaurantHelper.getRestaurantsCollection().orderBy("distance", Query.Direction.ASCENDING);
    }

    // -- GET ALL USERS FOR A RESTAURANT --
    public static Query getAllClients(String restoId){
        return RestaurantHelper.getRestaurantsCollection().document(restoId).collection("clientsToday");
    }

}