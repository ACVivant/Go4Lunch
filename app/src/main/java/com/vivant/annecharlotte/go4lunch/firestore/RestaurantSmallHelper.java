package com.vivant.annecharlotte.go4lunch.firestore;

import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.vivant.annecharlotte.go4lunch.models.RestaurantSmall;
import com.vivant.annecharlotte.go4lunch.neSertPlusARienJeCrois.RestaurantHelper;

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
    public static Task<Void> createRestaurant(String restoId, String restoName, String address) {
        RestaurantSmall restaurantToCreate = new RestaurantSmall(restoName, address);
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

}