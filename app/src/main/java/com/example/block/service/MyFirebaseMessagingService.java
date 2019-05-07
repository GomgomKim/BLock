package com.example.block.service;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.block.R;
import com.example.block.main.MainActivity;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private final static String TAG = "FCM_NOTI";
    public MyFirebaseMessagingService() {
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getNotification() !=null){
            String body = remoteMessage.getNotification().getBody();
            Log.d(TAG,"Notification Body : " + body);
            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.N_MR1){
                Log.d(TAG,"누가버전 이하");
                /////////////////////////////////////////////////////////////////////////////////////////////////////////////////

                ///////////////////////////////////////////////////////////////////////////////////////////////

                BitmapDrawable bitmapDrawable = (BitmapDrawable)getResources().getDrawable(R.mipmap.ic_launcher);
                Bitmap bitmap = bitmapDrawable.getBitmap();

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("NotificationMessage", "message");
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext()).
                        setLargeIcon(bitmap)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis()).
                                setShowWhen(true).
                                setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX)
                        .setContentTitle(getString(R.string.app_name))
                        .setDefaults(Notification.FLAG_AUTO_CANCEL)
                        .setFullScreenIntent(pendingIntent,true)
                        .setContentIntent(pendingIntent)
                        .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용


                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0,builder.build());
            }
            else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                Log.d(TAG,"오레오레오레오레오 이상");

                int importance = NotificationManager.IMPORTANCE_HIGH;
                String Noti_Channel_ID = "Block_Noti";
                String Noti_Channel_Group_ID = "Block_Noti_Group";

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                NotificationChannel notificationChannel = new NotificationChannel(Noti_Channel_ID,Noti_Channel_Group_ID,importance);

                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.putExtra("NotificationMessage", "message");
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);

                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

                if(notificationManager.getNotificationChannel(Noti_Channel_ID) != null){
                }
                else{
                    notificationManager.createNotificationChannel(notificationChannel);
                }

                notificationManager.createNotificationChannel(notificationChannel);
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(),Noti_Channel_ID)
                        .setLargeIcon(null).setSmallIcon(R.mipmap.ic_launcher)
                        .setWhen(System.currentTimeMillis()).setShowWhen(true)
                        .setAutoCancel(true).setPriority(NotificationCompat.PRIORITY_MAX)
                        .setFullScreenIntent(pendingIntent,true)
                        .setContentIntent(pendingIntent)
                        .setContentTitle(getString(R.string.app_name))
                        .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용

                notificationManager.notify(0,builder.build());
            }

//            NotificationManager notificationManager =
//                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//            NotificationCompat.Builder notificationBuilder = null;
//            if (Build.VERSION.SDK_INT >= 26) {
//                Log.e("오레오","오레오레오");
//                NotificationChannel mChannel = new NotificationChannel("andokdcapp", "andokdcapp", NotificationManager.IMPORTANCE_DEFAULT);
//                notificationManager.createNotificationChannel(mChannel);
//                notificationBuilder = new NotificationCompat.Builder(this,mChannel.getId());
//            } else {
//                notificationBuilder = new NotificationCompat.Builder(this);
//            }
//
//            notificationBuilder.setAutoCancel(true)
//                    .setSmallIcon(R.mipmap.ic_launcher)
//                    .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
//                    .setContentTitle(getString(R.string.app_name))
//                    .setContentText(body); // Firebase Console 에서 사용자가 전달한 메시지내용
////                    .setSound(defaultSoundUri);
//
//            notificationManager.notify(0
//                    // ID of notification
//                    , notificationBuilder.build());
        }
    }
}