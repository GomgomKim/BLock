package com.example.block.adapter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutionException;

public class ContactCmd {

    public ContactCmd() throws ExecutionException, InterruptedException{
        Runtime runtime = Runtime.getRuntime();
        Process process;
        String res = "-0-";
        try {
            String cmd = "top -n 1";
            process = runtime.exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line ;
            while ((line = br.readLine()) != null) {
                Log.i("test",line);
            }
        } catch (Exception e) {
            e.fillInStackTrace();
            Log.e("Process Manager", "Unable to execute top command");
        }
    }
}
