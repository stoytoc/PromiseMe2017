package com.example.hj.testproject;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class RingtonePlayingService extends Service {
    MediaPlayer media_song;
    int startid;
    private boolean isRunning;
    private String name;
    public IBinder onBind(Intent intent){
        return null;
    }
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("LocalService", "Received start id " + startId + ": " + intent);
        String state = intent.getExtras().getString("extra");
        name=intent.getExtras().getString("name");

        Log.e("Ringtone state:extra is",""+state);
        //set up the notification service
        switch(state){
            case "alarm on":
                startid =1;
                break;
            case "alarm off":
                startid =0;
                Log.d("State Id is",state);
                break;
            default:
                startid=0;
                break;
        }
        if(!this.isRunning&& startid == 1){
//음악은 안울리는데 알람on 설정해놓은것
            Log.e("there is no music","and you want start");
            media_song = MediaPlayer.create(this,R.raw.alarm3);
            //start the ringtone
            media_song.start();
            this.startid=0;
            NotificationManager notify_manager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //set up an intent that goes to the main activity
            Intent intent_main_activity = new Intent(this.getApplicationContext(),MainActivity.class);
            //make the notification parameters
            PendingIntent pending_intent_main_activity=PendingIntent.getActivity(this,0,intent_main_activity,0);
            Notification notification_popup=new Notification.Builder(this).setContentTitle("It's time to~")
                    .setContentText(name+" 습관을 할 시간이에요!")
                    .setContentIntent(pending_intent_main_activity)
                    .setSmallIcon(R.drawable.maincat)
                    .setAutoCancel(true)
                    .build();
            //set up the notification call command
            notify_manager.notify(0,notification_popup);
            Intent showStopActivity=new Intent(getApplicationContext(),StopActivity.class);
            showStopActivity.putExtra("name",name);
            startActivity(showStopActivity);
        }
        else if(this.isRunning&&startid==0){
            Log.e("there is music","and you want end");
            media_song.stop();
            media_song.reset();
            this.isRunning=false;
            this.startid=0;
        }
        //음악이 울리고 있고 알람off를 눌렀을때
        else if(!this.isRunning&&startid==0){
            Log.e("there is no music","and you want end");
            media_song.stop();
            media_song.reset();
            this.isRunning=false;
            this.startid=0;

        }
        //if there is music playing and the user pressed "alarm on"
        else if(this.isRunning&&startid==1){
            Log.e("there is music","and you want start");
            media_song = MediaPlayer.create(this,R.raw.alarm3);
            //start the ringtone
            media_song.start();
            NotificationManager notify_manager =(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            //set up an intent that goes to the main activity
            Intent intent_main_activity = new Intent(this.getApplicationContext(),MainActivity.class);
            //make the notification parameters
            PendingIntent pending_intent_main_activity=PendingIntent.getActivity(this,0,intent_main_activity,0);
            Notification notification_popup=new Notification.Builder(this).setContentTitle("It's time to~")
                    .setContentText(name+" 습관을 할 시간이에요!")
                    .setContentIntent(pending_intent_main_activity)
                    .setSmallIcon(R.drawable.maincat)
                    .setAutoCancel(false)
                    .build();
            notify_manager.notify(0,notification_popup);
            Intent showStopActivity=new Intent(getApplicationContext(),StopActivity.class);
            showStopActivity.putExtra("name",name);
            startActivity(showStopActivity);
            //set up the notification call command
            notify_manager.notify(0,notification_popup);
            this.isRunning=true;
            this.startid=1;
        }
        else{
            Log.e("else","somehow you reached this");
        }
        return START_NOT_STICKY;
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        this.isRunning=false;
        Toast.makeText(this, "on Destory called", Toast.LENGTH_SHORT).show();
    }
}
