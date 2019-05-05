package com.example.block.database;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class HostRequestPost {
    public String sender_phone;
    public String sender_name;
    public String receiver_token;
    public String door_id;

    public HostRequestPost(){

    }

    public HostRequestPost(String sender_phone, String sender_name, String receiver_token, String door_id) {
        this.sender_phone = sender_phone;
        this.sender_name = sender_name;
        this.receiver_token = receiver_token;
        this.door_id = door_id;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("sender_phone", sender_phone);
        result.put("sender_name", sender_name);
        result.put("receiver_token", receiver_token);
        result.put("door_id", door_id);
        return result;
    }
}
