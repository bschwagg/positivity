/*+----------------------------------------------------------------------
 ||
 ||  Class com.bschwagler.positivity 
 ||
 ||        Author:  Brad  Dec 22, 2014
 ||
 ||        Description:  
 ||                  
 ||
  +---------------------------------------------------------------------- */
package com.bschwagler.positivity;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 22, 2014
 *
 * Class AlarmReceiver.java Description: 
 *
 *  
 */
public class AlarmReceiver extends BroadcastReceiver {
	//Setup callback for the alarm

	private int msgCount = 0;

	@Override
	public void onReceive(Context c, Intent i) {

		//Do we have any special messages to pass on to the final handler?
		Bundle outExtras = new Bundle();
		Bundle inExtras = i.getExtras();
		boolean immediate = false;
		if(inExtras != null){
			//Here we want force the message to display
			if(inExtras.getString("message") != null){
				Globals.getInstance().forcedMessage = inExtras.getString("message");
				outExtras.putString("message", inExtras.getString("message")); //pass on the message to set
			}
		
			//Here we want to show the dialog immediately
			if(i.getStringExtra("immediate") != null){
				//pop up the dialog right away
				outExtras.putString("immediate", "true");
				immediate = true;
			}
			//Here we want to pop up a toast message
			String msg = i.getStringExtra("toastMsg");
			if(msg!=null)
				Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();
		}
		Bitmap bm = BitmapFactory.decodeResource(c.getResources(), R.drawable.ic_launcher_128);



		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(c, BackgroundActivity.class); //NOTE: passes on the intent to the backgroundactivity

		// Sets the Activity to start in a new, empty task
		outExtras.putInt("notificationId", msgCount); //store ID so we can cancel the notif
		resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		resultIntent.putExtras(outExtras);


		PendingIntent resultPendingIntent =
				PendingIntent.getActivity(
						c,
						0,
						resultIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

		doNotifFeedback(c);

		//Shall we pop this message up right now??
//		if(immediate){
//			
//			c.startActivity( resultIntent ); //TODO: Why does this not work?
//		} else {
			//Do a notification in the task bar
			NotificationCompat.Builder mBuilder =	new NotificationCompat.Builder(c)

			.setSmallIcon(R.drawable.ic_launcher_24)
			.setLargeIcon(bm)
			//				.setContentText("Hello World!");
			.setContentTitle("Positivity");
			mBuilder.setContentIntent(resultPendingIntent);
			//pop the notification in heads up on top of screen, similar to incoming calls
			//TODO: may have to setup ringtone or vibration to make this work as well
			mBuilder.setPriority(NotificationCompat.PRIORITY_MAX); 
			//mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC); //android 5.0 lock screen feature
			NotificationManager mNotificationManager =
					(NotificationManager) c.getSystemService(Context.NOTIFICATION_SERVICE);
			// mId allows you to update the notification later on.
			mNotificationManager.notify(msgCount , mBuilder.build());
			//The notification will then start a special service. The service is detached 
			//see: https://developer.android.com/guide/topics/ui/notifiers/notifications.html
//		}

		msgCount++; //Essentially a UID
	}

	/**
	 *  Activates feedback for vibration and audio alarms, depending on global settings
	 */
	private void doNotifFeedback(Context c) {
		// Vibrate the mobile phone
		if(Globals.getInstance().vibEnabled) {
			Vibrator vibrator = (Vibrator) c.getSystemService(Context.VIBRATOR_SERVICE);
			if(vibrator != null)
				vibrator.vibrate(800);
		}

		if(Globals.getInstance().noiseEnabled) {
			try {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION /*TYPE_ALARM*/);
				Ringtone r = RingtoneManager.getRingtone(c.getApplicationContext(), notification);
				r.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

}
