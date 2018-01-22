package com.dawnchorus.alarms;

import android.content.Context;
import android.content.Intent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.util.Log;

public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {
            // It is better to reset alarms using Background IntentService
            Intent i = new Intent(context, RebootService.class);
            ComponentName service = context.startService(i);

            if (null == service) {
                Log.e("TAG", "Could not start service ");
            }
            else {
                Log.e("TAG", "Successfully started service ");
            }

        } else {
        Log.e("TAG", "Received unexpected intent " + intent.toString());
        }
    }

}
