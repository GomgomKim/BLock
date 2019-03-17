package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class MemberPost {
    public String user_name;
    public String home_door_id;

    public MemberPost(){

    }

    public MemberPost(String user_name, String home_door_id) {
        this.user_name = user_name;
        this.home_door_id = home_door_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_name", user_name);
        result.put("home_door_id", home_door_id);
        return result;
    }
}
