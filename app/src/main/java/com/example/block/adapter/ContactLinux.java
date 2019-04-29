package com.example.block.adapter;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ContactLinux {
    public ContactLinux() {
        String result = runProcess();
        Log.i("gomgomKim", result);
    }

    public String runProcess() throws  RuntimeException {
        try {
            // 리눅스 명령어 실행
            Process process = Runtime.getRuntime().exec("/system/bin/ls");

            // 결과읽기
            BufferedReader reader = new BufferedReader((new InputStreamReader(process.getInputStream())));
            int read;
            char[] buffer = new char[4096];
            StringBuffer output = new StringBuffer();
            while((read = reader.read(buffer)) > 0){
                output.append(buffer, 0, read);
            }
            reader.close();

            // 명령문이 종료될 때까지 기다리기
            process.waitFor();

            return  output.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return "";
    }
}
