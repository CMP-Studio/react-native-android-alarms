package com.dawnchorus.alarms;

import android.app.IntentService;

import android.content.SharedPreferences;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.content.SharedPreferences;
import android.app.Activity;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class RebootService extends IntentService {

    public RebootService() {
        super("RebootService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        // Your code to reset alarms.
        // All of these will be done in Background and will not affect
        // on phone's performance
        // Put the alarm into the preferences
	    SharedPreferences alarms = getApplicationContext().getSharedPreferences("Alarms", 0);
	    Map<String,?> keys = alarms.getAll();

	    for(Map.Entry<String,?> entry : keys.entrySet()){ 
            String id = entry.getKey();  
            long timestamp = alarms.getLong(entry.getKey(), 0);
            if (timestamp != 0) {
            	setAlarmOnReboot(entry.getKey(), timestamp, false);
            }
 		}
    }

    public final void setAlarmOnReboot(String id, long timestamp, boolean inexact) {
	    PendingIntent pendingIntent = createPendingIntent(id);
	    long timestampLong = (long)timestamp; // React Bridge doesn't understand longs
	    // get the alarm manager, and schedule an alarm that calls the receiver
	    // We will use setAlarmClock because we want an indicator to show in the status bar.
	    // If you want to modify it and are unsure what to method to use, check https://plus.google.com/+AndroidDevelopers/posts/GdNrQciPwqo
	    
	    if(!inexact) {
	//      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
	//        getAlarmManager().setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, timestampLong, pendingIntent);
	      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
	        getAlarmManager().setAlarmClock(new AlarmManager.AlarmClockInfo(timestampLong, pendingIntent), pendingIntent);
	      else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT)
	        getAlarmManager().setExact(AlarmManager.RTC_WAKEUP, timestampLong, pendingIntent);
	      else
	        getAlarmManager().set(AlarmManager.RTC_WAKEUP, timestampLong, pendingIntent);
	    } else {
	      getAlarmManager().set(AlarmManager.RTC_WAKEUP, timestampLong, pendingIntent);
	    }
	  }

	private PendingIntent createPendingIntent(String id) {
	    Context context = getApplicationContext();
	    // create the pending intent
	    Intent intent = new Intent(context, AlarmReceiver.class);
	    // set unique alarm ID to identify it. Used for clearing and seeing which one fired
	    // public boolean filterEquals(Intent other) compare the action, data, type, package, component, and categories, but do not compare the extra
	    intent.setData(Uri.parse("id://" + id));
	    intent.setAction(String.valueOf(id));
	    return PendingIntent.getBroadcast(context, 0, intent, 0);
	}

	private AlarmManager getAlarmManager() {
	    return (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
	}
}