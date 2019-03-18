package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DoorPost {
    public String door_id;

    public DoorPost(){

    }

    public DoorPost(String door_id) {
        this.door_id = door_id;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("door_id", door_id);
        return result;
    }
}
