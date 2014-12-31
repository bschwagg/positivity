/*+----------------------------------------------------------------------
 ||
 ||  Class com.bschwagler.positivity 
 ||
 ||        Author:  Brad  Dec 31, 2014
 ||
 ||        Description:  
 ||                  
 ||
  +---------------------------------------------------------------------- */
package com.bschwagler.positivity;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 31, 2014
 *
 * Class PushReceiver.java Description: 
 *
 *  
 */
public class PushReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Bundle extras = intent.getExtras();
		String fullmessage = extras != null ? extras.getString("com.parse.Data") : "";
		if(fullmessage != null){
			JSONObject json;
			try {
				json = new JSONObject(fullmessage);

				String channel = intent.getExtras().getString("com.parse.Channel");  
				String action = intent.getAction();  
				Log.d("push msg", "got action " + action + " on channel " + channel + " with:");  
				Iterator itr = json.keys();  
				while (itr.hasNext()) {  
					String key = (String) itr.next();  
					if (key.equals("alert"))  
					{  
						kickOffMsg(context, json.getString(key) );
					}  
					Log.d("push msg", "..." + key + " => " + json.getString(key));  
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}  
		}
	}

	/**
	 * @param string
	 */
	private void kickOffMsg(Context c, String string) {
		//Create an alarm which will fire off our AlarmReceiver..
		Intent intent = new Intent(c, AlarmReceiver.class);
		Bundle extras = new Bundle();
		extras.putString("immediate", "true"); //pass a flag that we want to pop up the dialog right away. No notification manager.
		extras.putString("message", string);
		intent.putExtras(extras);
		//extras.putString("toastMsg", toastMsg);
		PendingIntent pi = PendingIntent.getBroadcast( c, 0, intent, /*PendingIntent.FLAG_UPDATE_CURRENT)*/	0 );
		AlarmManager am = (AlarmManager)(c.getSystemService( Context.ALARM_SERVICE ));
		am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()/*ms*/, pi ); //single shot alarm
		//am.setRepeating(AlarmManager.RTC_WAKEUP, /*ms*/, AlarmManager.INTERVAL_DAY, pi); //daily alarm


	}
}