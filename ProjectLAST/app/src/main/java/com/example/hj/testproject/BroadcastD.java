package com.example.hj.testproject;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
/**
 * Created by multimedia on 2017-05-30.
 */
public class BroadcastD extends BroadcastReceiver{
    public void onReceive(Context context, Intent intent) {
        Intent service_intent=new Intent(context,BroadcastS.class);
        context.startService(service_intent);
    }
}
