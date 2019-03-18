package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class GuestPost {
    public String user_id;
    public String user_name;
    public String door_id;
    public String start_time;
    public String end_time;

    public GuestPost(){

    }

    public GuestPost(String user_id, String user_name, String door_id, String start_time, String end_time) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.door_id = door_id;
        this.start_time = start_time;
        this.end_time = end_time;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("user_name", user_name);
        result.put("door_id", door_id);
        result.put("start_time", start_time);
        result.put("end_time", end_time);
        return result;
    }
}
