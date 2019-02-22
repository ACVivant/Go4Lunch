package com.vivant.annecharlotte.go4lunch.Firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.Models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.NeSertPlusARienJeCrois.RestaurantHelper;

import java.util.List;

/**
 * Created by Anne-Charlotte Vivant on 22/02/2019.
 */
public class RestaurantSmallHelper {
    private static final String COLLECTION_NAME = "restaurants";

    // --- COLLECTION REFERENCE ---
    public static CollectionReference getRestaurantsCollection(){
        return FirebaseFirestore.getInstance().collection(COLLECTION_NAME);
    }

    // --- CREATE ---
    public static Task<Void> createRestaurant(String restoId, String restoName) {
        RestaurantSmall restaurantToCreate = new RestaurantSmall(restoName);
        return RestaurantSmallHelper.getRestaurantsCollection().document(restoId).set(restaurantToCreate);
    }

    // --- GET ---
    public static Task<DocumentSnapshot> getRestaurant(String restoId){
        return RestaurantSmallHelper.getRestaurantsCollection().document(restoId).get();
    }

    // --- UPDATE NAME---
    public static Task<Void> updateRestoName(String restoName, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("restoname", restoName);
    }

    // --- UPDATE TODAY'S USERS---
    public static Task<Void> updateClientsTodayList(List<String> clientsTodayList, String restoId) {
        return RestaurantHelper.getRestaurantsCollection().document(restoId).update("clientsTodayList", clientsTodayList);
    }

    // -- GET ALL NEARBY RESTAURANT --
    public static Query getAllTodayRestaurants(){
        return RestaurantHelper.getRestaurantsCollection().orderBy("restoName", Query.Direction.ASCENDING);
    }

}