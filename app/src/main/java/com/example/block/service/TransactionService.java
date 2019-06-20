package com.example.block.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;

import com.example.block.R;
import com.example.block.web3j.DeployTest;
import com.example.block.web3j.GetAuth;
import com.example.block.web3j.GetGuest;
import com.example.block.web3j.GetHost;
import com.example.block.web3j.GetOpenHistory;
import com.example.block.web3j.SetGuest;
import com.example.block.web3j.SetHistory;
import com.example.block.web3j.SetHost;

import java.util.concurrent.ExecutionException;


public class TransactionService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
        startForegroundService();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String door_id = intent.getExtras().getString("door_id");
        String user_token = intent.getExtras().getString("user_token");

        String state = intent.getExtras().getString("state");

        String sender = intent.getExtras().getString("sender");
        String user_id = intent.getExtras().getString("user_id");
        String user_name = intent.getExtras().getString("user_name");
        String start_time = intent.getExtras().getString("start_time");
        String end_time = intent.getExtras().getString("end_time");

        String cur_time = intent.getExtras().getString("cur_time");

        String address = intent.getExtras().getString("address");

        String tool = intent.getExtras().getString("tool");

        switch (state){
            case "deploy":
                try {
                    new DeployTest(this, door_id, user_token, user_id, user_name);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
            case "set":
                String type = intent.getExtras().getString("type");
                switch (type){
                    case "host":
                        try {
                            new SetHost(this, user_id, address, cur_time, sender);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "guest":
                        try {
                            new SetGuest(this, user_id, start_time, end_time, address, cur_time, sender);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;
            case "get":
                String type_get = intent.getExtras().getString("type");
                switch (type_get){
                    case "host":
                        try {
                            new GetHost(this, address, user_id, tool);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "guest":
                        try {
                            new GetGuest(this, address, user_id, cur_time);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }
                break;

            case "openHistory":
                try {
                    new GetOpenHistory(this, address);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case "setHistory":
                String type_history = intent.getExtras().getString("type");
                try {
                    new SetHistory(this, sender, type_history, address, cur_time);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;

            case "getAuth":
                try {
                    new GetAuth(this, address);
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                break;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    void startForegroundService() {
        NotificationCompat.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            String CHANNEL_ID = "block service";
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
                    "block service",
                    NotificationManager.IMPORTANCE_DEFAULT);

            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
                    .createNotificationChannel(channel);

            builder = new NotificationCompat.Builder(this, CHANNEL_ID);
        } else {
            builder = new NotificationCompat.Builder(this);
        }
        builder.setSmallIcon(R.mipmap.ic_launcher);

        startForeground(1, builder.build());
    }

}


