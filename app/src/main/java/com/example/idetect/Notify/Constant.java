package com.example.idetect.Notify;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class Constant {

    public static final APIService apiService = Client.getClient("https://fcm.googleapis.com/").create(APIService.class);
    public static final DatabaseReference tokens = FirebaseDatabase.getInstance().getReference("Tokens");
}
