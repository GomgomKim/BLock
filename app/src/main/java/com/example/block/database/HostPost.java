package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class HostPost {
    public String user_id;
    public String user_name;
    public String door_id;

    public HostPost(){

    }

    public HostPost(String user_id, String user_name, String door_id) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.door_id = door_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("user_id", user_id);
        result.put("user_name", user_name);
        result.put("door_id", door_id);
        return result;
    }
}
