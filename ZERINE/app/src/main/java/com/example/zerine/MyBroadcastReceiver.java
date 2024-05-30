package com.example.zerine;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;



public class MyBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)){
            Intent serviceIntent = new Intent(context, ForegroundServices.class);
            context.startForegroundService(serviceIntent);

        }

    }
}
