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
import android.os.SystemClock;
import android.widget.Toast;

/**
 * @author Brad
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
			if(GlobalsAreBad.getInstance().firstWakeAlarm && wakeAlarmDay != currDayNum){
				
				final String name = settings.getString("username", "");
				Toast.makeText(context, "Good morning " + name, Toast.LENGTH_SHORT).show(); //TEST
				//We have a new day.. lets fire an alarm!
				fireOffAlarm(context);
				//Remember we already did the alarm for today
				SharedPreferences.Editor editor = settings.edit();
				editor.putInt("wakeAlarmDay", currDayNum);
				editor.commit();
			}
		}
	}

	private void fireOffAlarm(Context c) {	
		//Create an intent to open up the background activity immediately
		//Don't put it in the task bar thing
		//no need to fire off vibration and noise
		Intent bgIntent = new Intent(c, BackgroundActivity.class);
		bgIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		c.startActivity( bgIntent );	
	}

}
