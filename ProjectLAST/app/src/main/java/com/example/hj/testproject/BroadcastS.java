package com.example.hj.testproject;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;
/**
 * Created by multimedia on 2017-05-30.
 */
public class BroadcastS extends Service {
    public IBinder onBind(Intent intent){
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        SharedPreferences aa=getSharedPreferences("aa",0);
        int successRate=aa.getInt("four",0);
        int numOfData=aa.getInt("first",0);
        int unComplete=aa.getInt("third",0);

        if(successRate==100){
            NotificationManager notify_manager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //set up an intent that goes to the main activity
            Intent intent_main_activity = new Intent(this.getApplicationContext(),MainActivity.class);
            //make the notification parameters
            PendingIntent pending_intent_main_activity=PendingIntent.getActivity(this,0,intent_main_activity,0);
            Notification notification_popup=new Notification.Builder(this).setContentTitle("Yes!!")
                    .setContentText("오늘의 목표를 모두 달성했어요!")
                    .setContentIntent(pending_intent_main_activity)
                    .setSmallIcon(R.drawable.maincat)
                    .setAutoCancel(true)
                    .build();
            notify_manager.notify(0,notification_popup);
           stopService(intent);
            return START_NOT_STICKY;
        }
        else{
        NotificationManager notify_manager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        //set up an intent that goes to the main activity
        Intent intent_main_activity = new Intent(this.getApplicationContext(),MainActivity.class);
        //make the notification parameters
        PendingIntent pending_intent_main_activity=PendingIntent.getActivity(this,0,intent_main_activity,0);
        Notification notification_popup=new Notification.Builder(this).setContentTitle("오늘의 목표 습관"+numOfData+"개 중에서")
                .setContentText(unComplete+"개가 남았습니다!")
                .setContentIntent(pending_intent_main_activity)
                .setSmallIcon(R.drawable.maincat)
                .setAutoCancel(true)
                .build();
        //set up the notification call command
        notify_manager.notify(0,notification_popup);
        }
        return START_REDELIVER_INTENT;
    }
}
