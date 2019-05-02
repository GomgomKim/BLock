package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class MemberPost {
    public String user_name;
    public String home_door_id;
    public String token;
    public String user_id;


    public MemberPost(){

    }

    public MemberPost(String user_name, String home_door_id, String token, String user_id) {
        this.user_name = user_name;
        this.home_door_id = home_door_id;
        this.token = token;
        this.user_id = user_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_name", user_name);
        result.put("home_door_id", home_door_id);
        result.put("token", token);
        result.put("user_id", user_id);
        return result;
    }
}