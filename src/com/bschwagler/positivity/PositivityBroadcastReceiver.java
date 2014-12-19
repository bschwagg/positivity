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
import android.widget.Toast;


/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class PositivityBroadcastReceiver.java Description: 
 *
 * Handles any system event messages that need to be received, such as user logs in,
 * locks device, location via wifi, etc. This class is a dispatch for other services/activities to run.
 *  
 */
public class PositivityBroadcastReceiver extends BroadcastReceiver {

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
			if(Globals.getInstance().firstWakeAlarm && wakeAlarmDay != currDayNum){
				
				final String name = settings.getString("username", "");
				Toast.makeText(context, "Good morning " + name, Toast.LENGTH_SHORT).show(); //TEST
				//We have a new day.. lets fire an alarm!
				fireOffAlarm(context, 0, null);
				//Remember we already did the alarm for today
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("wakeAlarmDay", currDayNum);
				editor.commit();
			}
			
			//Check the minutes-on-phone alarm if needed
			if(Globals.getInstance().phoneUseAlarm){
				fireOffAlarm(context, Globals.getInstance().phoneUseAlarmMinutes, 
						"Already on phone " +  Globals.getInstance().phoneUseAlarmMinutes + " minutes");
			}
		} else if(true) //locks phone
		{
			//TODO: cancel alarm
		}
	}

	private void fireOffAlarm(Context c, int seconds, String toastMsg) {	
		//Create an intent to open up the background activity immediately
		//Don't put it in the task bar thing
		//no need to fire off vibration and noise
		//		Intent bgIntent = new Intent(c, BackgroundActivity.class);
		//		bgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		//		c.startActivity( bgIntent );	
		//		
		Intent broadcast_intent = new Intent("com.bschwagler.wakeup");
		Bundle extras = new Bundle();
		
		extras.putString("immediate", "true"); //pass a flag that we want to pop up the dialog right away. No notification manager.
		if(toastMsg!=null)
			extras.putString("toastMsg", toastMsg);
		broadcast_intent.putExtras(extras);
		PendingIntent pi = PendingIntent.getBroadcast( c, 100, broadcast_intent, 0 );
		AlarmManager am = (AlarmManager)(c.getSystemService( Context.ALARM_SERVICE ));
		am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + seconds*60*1000 /*ms*/, pi );
	}

}
