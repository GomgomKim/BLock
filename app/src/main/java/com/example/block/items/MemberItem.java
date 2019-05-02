package com.example.block.items;

public class MemberItem {
    private String phone_num;
    private String user_name;
    private String home_door_id;
    private String token;

    public MemberItem(String phone_num, String user_name, String home_door_id,String token){
        this.phone_num = phone_num;
        this.user_name = user_name;
        this.home_door_id = home_door_id;
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setphone_num(String phone_num){  this.phone_num = phone_num; }
    public String getphone_num(){ return this.phone_num; }

    public void setUser_name(String user_name){  this.user_name = user_name; }
    public String getUser_name(){ return this.user_name; }

    public void setHome_door_id(String home_door_id){  this.phone_num = home_door_id; }
    public String getHome_door_id(){ return this.home_door_id; }

}
