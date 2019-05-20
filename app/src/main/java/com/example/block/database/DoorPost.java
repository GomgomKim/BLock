package com.example.block.database;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;

@IgnoreExtraProperties
public class DoorPost {
    public String door_id;
    public String deploy_address;

    public DoorPost(){

    }

    public DoorPost(String door_id, String dep_add) {
        this.door_id = door_id;
        this.deploy_address = dep_add;

    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("door_id", door_id);
        result.put("deploy_address", deploy_address);
        return result;
    }
}
