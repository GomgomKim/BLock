package com.example.block.database;

import android.provider.BaseColumns;

public class DataBases {
    public static final class CreateDB implements BaseColumns {
        public static final String USERID = "user_id";
        public static final String USERNAME = "user_name";
        public static final String USERINTRO = "user_intro";
        public static final String HOMEDOORID = "home_door_id";
        public static final String MEMBER = "member";
        public static final String CREATEMEMBER = "create table if not exists "+MEMBER+"("
                +_ID+" integer primary key autoincrement, "
                +USERID+" integer not null , "
                +USERNAME+" text not null , "
                +USERINTRO+" text , "
                +HOMEDOORID+" integer );";



        public static final String DOORID = "door_id";
        public static final String DOOR = "door";
        public static final String CREATEDOOR = "create table if not exists "+DOOR+"("
                +_ID+" integer primary key autoincrement, "
                +DOORID+" integer not null );";



        public static final String HOSTDOORID = "door_id";
        public static final String HOSTUSERID = "user_id";
        public static final String HOSTUSERNAME = "user_name";
        public static final String HOST = "host";
        public static final String CREATEHOST = "create table if not exists "+HOST+"("
                +_ID+" integer primary key autoincrement, "
                +HOSTDOORID+" integer not null , "
                +HOSTUSERID+" integer not null , "
                +HOSTUSERNAME+" text not null );";



        public static final String GUESTDOORID = "door_id";
        public static final String GUESTUSERID = "user_id";
        public static final String GUESTUSERNAME = "user_name";
        public static final String STARTTIME = "start_time";
        public static final String ENDTIME = "end_time";
        public static final String GUEST = "guest";
        public static final String CREATEGUEST = "create table if not exists "+GUEST+"("
                +_ID+" integer primary key autoincrement, "
                +GUESTDOORID+" integer not null , "
                +GUESTUSERID+" integer not null , "
                +GUESTUSERNAME+" text not null , "
                +STARTTIME+" datetime not null, "
                +ENDTIME+" datetime not null );";
    }
}
