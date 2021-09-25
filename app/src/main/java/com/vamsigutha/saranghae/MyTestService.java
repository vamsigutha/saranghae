package com.vamsigutha.saranghae;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.legacy.content.WakefulBroadcastReceiver;

public class MyTestService extends IntentService {

    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    public MyTestService() {
        super("MyTestService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        sharedPreferences =  getSharedPreferences("token",0);
        editor = sharedPreferences.edit();
        editor.putInt("sentHearts",0);
        editor.putInt("receivedHearts",0);
        editor.apply();
        // Do the task here
        Log.i("MyTestService", "Service running");
        // Release the wake lock provided by the WakefulBroadcastReceiver.
        WakefulBroadcastReceiver.completeWakefulIntent(intent);
    }
}
