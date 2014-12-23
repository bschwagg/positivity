package com.bschwagler.positivity;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class TimePickerFragment.java Description: 
 *
 *  Helper class to pick a time
 *  
 */
public class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {

	int hour, minute;
	PendingIntent pi;
	AlarmManager am;
	public boolean wasCancelled;
	TextView tv;
	CheckBox checkBox;
	AlarmListAdapter aa;

	 /**
	 * 
	 */
	public TimePickerFragment(AlarmListAdapter a) {
		aa = a;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {


		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));

		//For some reason the only way to intercept "cancel" button is this way.  Android limitation.
		//		tpDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ("cancel"), new DialogInterface.OnClickListener() {
		//			public void onClick(DialogInterface dialog, int which) {
		//				if (which == DialogInterface.BUTTON_NEGATIVE) {
		//					//nothing done
		//				}
		//				Toast.makeText(getActivity().getApplicationContext(), "CLicked " + which, Toast.LENGTH_SHORT).show();
		//			}
		//		});

		return tpDialog;
	}

	public void onTimeSet(TimePicker view, int hourOfDaySel, int minuteSel) {
		// Do something with the time chosen by the user
		final Calendar now = Calendar.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDaySel);
		calendar.set(Calendar.MINUTE, minuteSel);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0); 
		
		if(calendar.before(now))
			calendar.add(Calendar.DAY_OF_YEAR, 1); //Don't trigger if time is earlier in the day!

		Globals.getInstance().dailyAlarmList.add( calendar );
		aa.update(); //refresh view
		
		//Would like to make a global alarm manager class. However can't access main Activity. How? TODO
			
		//Create an alarm which will fire off our AlarmReceiver..
		Intent intent = new Intent(getActivity() /*MainActivity.this*/, AlarmReceiver.class);
		//				Bundle extras = new Bundle();
		//				extras.putString("immediate", "true"); //pass a flag that we want to pop up the dialog right away. No notification manager.
		//				intent.putExtras(extras);
		//extras.putString("toastMsg", toastMsg);
		//store ID by calendar time in milliseconds
		PendingIntent pi = PendingIntent.getBroadcast( getActivity() /*MainActivity.this*/, (int) calendar.getTimeInMillis(), intent, /*PendingIntent.FLAG_UPDATE_CURRENT)*/	0 );
		AlarmManager am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));
		//am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()/*ms*/, pi ); //single shot alarm
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis()/*ms*/, AlarmManager.INTERVAL_DAY, pi); //daily alarm	
	}
}