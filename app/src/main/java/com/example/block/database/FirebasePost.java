package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class FirebasePost {
    public int user_id;
    public String user_name;
    public int home_door_id;

    public FirebasePost(){
        // Default constructor required for calls to DataSnapshot.getValue(FirebasePost.class)
    }

    public FirebasePost(int user_id, String user_name, int home_door_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.home_door_id = home_door_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("user_name", user_name);
        result.put("home_door_id", home_door_id);
        return result;
    }
}
