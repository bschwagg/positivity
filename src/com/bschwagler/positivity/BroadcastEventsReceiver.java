/**
 * 
 */
package com.bschwagler.positivity;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;


/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class BroadcastEventsReceiver.java Description: 
 *
 * Handles any system event messages that need to be received, such as user logs in,
 * locks device, location via wifi, etc. This class is a dispatch for other services/activities to run.
 *  
 */
public class BroadcastEventsReceiver extends BroadcastReceiver {

	final int ONE_SHOT_ALARM_ID = 100; //constant alarm ID
	/* (non-Javadoc)
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context, android.content.Intent)
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		//Sent when the user is fully logged in
		if(intent.getAction().equals(Intent.ACTION_USER_PRESENT)){		
			//Check and set the wake alarm if needed.
			final SharedPreferences settings = context.getSharedPreferences("UserData", 0);
			int wakeAlarmDay = settings.getInt("wakeAlarmDay", -1);
			int currDayNum = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);
			//Make sure it's a new day and past 4am
			if(Globals.getInstance().firstWakeAlarm && wakeAlarmDay != currDayNum && Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 4){
				//If it's still the morning then shoot off the alarm
				if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY) < 12) {
					final String name = settings.getString("username", "");
					Toast.makeText(context, "Good morning " + name, Toast.LENGTH_SHORT).show(); //TEST
					//We have a new day.. lets fire an alarm!
					fireOffAlarm(context, 0, null);
				}
				//Remember we already did the alarm for today
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("wakeAlarmDay", currDayNum);
				editor.commit();
			}
			
			//Check the minutes-on-phone alarm if needed
			if(Globals.getInstance().phoneUseAlarm){
				fireOffAlarm(context, Globals.getInstance().phoneUseAlarmMinutes, 
						"Already on phone " +  Globals.getInstance().phoneUseAlarmMinutes + " minutes..");
			}
		} else if(intent.getAction().equals(Intent.ACTION_SCREEN_OFF)){		 //locks phone
			Log.d("phone use", "Canceling phone usage alarm");
			cancelAlarm(context);
		}
	}

	private void fireOffAlarm(Context c, int seconds, String toastMsg) 
	{	
		//Create an intent to open up the background activity immediately
		//Don't put it in the task bar thing
		//no need to fire off vibration and noise
		//		Intent bgIntent = new Intent(c, BackgroundActivity.class);
		//		bgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		c.startActivity( bgIntent );	
		//		
		Intent broadcast_intent = new Intent(c /*MainActivity.this*/, AlarmReceiver.class);
		Bundle extras = new Bundle();
		
		extras.putString("immediate", "true"); //pass a flag that we want to pop up the dialog right away. No notification manager.
		if(toastMsg!=null)
			extras.putString("toastMsg", toastMsg);
		broadcast_intent.putExtras(extras);
		PendingIntent pi = PendingIntent.getBroadcast( c, ONE_SHOT_ALARM_ID, broadcast_intent, 0 );
		AlarmManager am = (AlarmManager)(c.getSystemService( Context.ALARM_SERVICE ));
		am.cancel(pi); //cancel old one if it still exists
		am.set( AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + seconds*60*1000 /*ms*/, pi );
		Log.d("alarm", "setting timer alarm for " + seconds);
	}
	
	private void cancelAlarm(Context c)
	{
		Intent broadcast_intent = new Intent(c /*MainActivity.this*/, AlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast( c, ONE_SHOT_ALARM_ID, broadcast_intent, 0 );
		AlarmManager am = (AlarmManager)(c.getSystemService( Context.ALARM_SERVICE ));
		am.cancel(pi);
		Log.d("alarm", "canceling alarm");
	}

}
