package com.example.block.service;

import android.app.NotificationChannel;
import android.app.NotificationManager;

import android.content.Context;
import android.os.Build;

import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.block.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = "FCM_MESSAGE";
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() !=null){
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG,"Notification Body : " + body);

//            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext())
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setContentTitle(getString(R.string.app_name)) // 알림 영역에 노출 될 타이틀
//                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용
//
//            NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
//            notificationManagerCompat.notify(0x1001, notificationBuilder.build());


//            PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent,
//                    PendingIntent.FLAG_ONE_SHOT);
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

            NotificationCompat.Builder notificationBuilder = null;
            if (Build.VERSION.SDK_INT >= 26) {
                Log.e("오레오","오레오레오");
                NotificationChannel mChannel = new NotificationChannel("andokdcapp", "andokdcapp", NotificationManager.IMPORTANCE_DEFAULT);
                notificationManager.createNotificationChannel(mChannel);
                notificationBuilder = new NotificationCompat.Builder(this,mChannel.getId());
            } else {
                notificationBuilder = new NotificationCompat.Builder(this);
            }

            notificationBuilder.setAutoCancel(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
                    .setContentTitle(getString(R.string.app_name))
                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용
//                    .setSound(defaultSoundUri);

            notificationManager.notify(0
                    // ID of notification
                    , notificationBuilder.build());
        }
    }
}
