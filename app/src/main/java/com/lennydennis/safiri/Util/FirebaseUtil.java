package com.lennydennis.safiri.Util;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.lennydennis.safiri.TravelDeal;

import java.util.ArrayList;

public class FirebaseUtil {
    public static FirebaseDatabase sFirebaseDatabase;
    public static DatabaseReference sDatabaseReference;
    public static FirebaseUtil sFirebaseUtil;
    public static ArrayList<TravelDeal> sTravelDeals;

    private FirebaseUtil() {
    }

    public  static void openFirebaseReference(String reference){
        if (sFirebaseUtil == null){
            sFirebaseUtil = new FirebaseUtil();
            sFirebaseDatabase = FirebaseDatabase.getInstance();
        }
        sTravelDeals = new ArrayList<TravelDeal>();
        sDatabaseReference = sFirebaseDatabase.getReference().child(reference);
    }
}
