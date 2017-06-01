package com.example.hj.testproject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * Created by multimedia on 2017-05-25.
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("we are in the receiver.","Yeah!");
        //fetch extra strings from the intent
        String get_your_string = intent.getExtras().getString("extra");
        String name=intent.getExtras().getString("name");
        Log.e("What is the key?.",""+get_your_string);
        //pass the extra string from main activity
        //create an intent to the ringtone service
        Intent service_intent=new Intent(context,RingtonePlayingService.class);
        service_intent.putExtra("extra",get_your_string);
        service_intent.putExtra("name",name);
        //start the ringtone service
        context.startService(service_intent);
    }
}
