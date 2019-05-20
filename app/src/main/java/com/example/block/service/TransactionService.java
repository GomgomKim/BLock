package com.example.block.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.example.block.web3j.DeployTest;
import com.example.block.web3j.GetGuest;
import com.example.block.web3j.GetHost;
import com.example.block.web3j.SetGuest;
import com.example.block.web3j.SetHost;

import java.util.concurrent.ExecutionException;


public class TransactionService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String door_id = intent.getExtras().getString("door_id");
        String user_token = intent.getExtras().getString("user_token");

        String state = intent.getExtras().getString("state");

//        String type = intent.getExtras().getString("type");

        String user_id = intent.getExtras().getString("user_id");
        String user_name = intent.getExtras().getString("user_name");
        String start_time = intent.getExtras().getString("start_time");
        String end_time = intent.getExtras().getString("end_time");

        String address = intent.getExtras().getString("address");

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
                            new SetHost(this, user_id, address);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "guest":
                        try {
                            new SetGuest(this, user_id, start_time, end_time, address);
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
                            new GetHost(this, address);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                    case "guest":
                        try {
                            new GetGuest(this, address);
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        break;
                }
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


}
