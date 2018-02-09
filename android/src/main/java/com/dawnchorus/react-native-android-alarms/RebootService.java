package com.dawnchorus.alarms;

import android.app.IntentService;
import 	android.content.res.Resources;
import android.graphics.drawable.Drawable;

import android.content.SharedPreferences;
import 	android.support.v4.content.res.ResourcesCompat;

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
import java.lang.String;

import android.graphics.BitmapFactory;
import android.graphics.Bitmap;

import android.app.Notification;
import android.app.NotificationManager;
import android.support.v4.app.NotificationCompat;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

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
	    // See if we missed an alarm
	    // If we did, we will launch app which will re-schedule alarms
	    boolean missedAlarm = false;
	    String missedAlarms = "";
	    for(Map.Entry<String,?> entry : keys.entrySet()){ 
            String id = entry.getKey();  
            long timestamp = alarms.getLong(entry.getKey(), 0);
           	long currentTime = System.currentTimeMillis();
           	if (timestamp != 0 && currentTime - timestamp >= 0) {
           		missedAlarm = true;
           		missedAlarms += ("," + id + ",");
           	} else {
           		if (timestamp != 0) setAlarmOnReboot(entry.getKey(), timestamp, false);
           	}
 		}

 		// If we did not miss an alarm, re schedule upcoming alarms in the background
 		if (missedAlarm) {
 			notifyUserOfMissedAlarm(getApplicationContext(), missedAlarms);
 		} 
    }

    private void notifyUserOfMissedAlarm(Context context, String missedAlarms) {
      Resources res = context.getResources();
      String packageName = context.getApplicationContext().getPackageName();
      Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

      String smallIconName = "ic_alarm";
      int smallIconResId = res.getIdentifier(smallIconName, "drawable", packageName);

      String largeIconName = "notification_icon";
      int largeIconResId = res.getIdentifier(largeIconName, "drawable", packageName);
      Bitmap largeIconBitmap = BitmapFactory.decodeResource(res, largeIconResId);

      NotificationCompat.Builder builder =
      new NotificationCompat.Builder(this)
      .setSmallIcon(smallIconResId)
      .setLargeIcon(largeIconBitmap)
      .setContentTitle("You missed an alarm!")
      .setContentText("Tap to re-schedule.")
      .setAutoCancel(true)
      .setPriority(1)
      .setDefaults(Notification.DEFAULT_VIBRATE);

      launchIntent.putExtra("missedAlarms", missedAlarms);
      
      PendingIntent contentIntent = PendingIntent.getActivity(this, 0, launchIntent,
         PendingIntent.FLAG_UPDATE_CURRENT);
      builder.setContentIntent(contentIntent);

      // Add as notification
      NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
      manager.notify(0, builder.build());
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

	 private void launchApplicationBecauseMissedAlarm(Context context, String missedAlarms) {
        String packageName = context.getApplicationContext().getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);

        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        launchIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        launchIntent.putExtra("missedAlarms", missedAlarms);

        context.startActivity(launchIntent);

        Log.i("ReactNativeAppLauncher", "AlarmReceiver: Launching: " + packageName + "missedAlarms " + missedAlarms);
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