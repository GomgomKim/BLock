package com.example.block.items;

public class MemberItem {
    private String user_id;
    private String user_name;
    private String home_door_id;

    public MemberItem(String user_id, String user_name, String home_door_id){
        this.user_id = user_id;
        this.user_name = user_name;
        this.home_door_id = home_door_id;
    }

    public void setUser_id(String user_id){  this.user_id = user_id; }
    public String getUser_id(){ return this.user_id; }

    public void setUser_name(String user_name){  this.user_name = user_name; }
    public String getUser_name(){ return this.user_name; }

    public void setHome_door_id(String home_door_id){  this.user_id = home_door_id; }
    public String getHome_door_id(){ return this.home_door_id; }

}
