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

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		//get a handle to the widgets we're modifying when cancelled
		tv = (TextView)(getActivity().findViewById(R.id.text_time_daily));
		checkBox = (CheckBox) getActivity().findViewById(R.id.alarm_time);

		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);

		pi = PendingIntent.getBroadcast( getActivity(), 1/*id*/, new Intent("com.bschwagler.wakeup"), 0 );
		am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));

		// Create a new instance of TimePickerDialog and return it
		TimePickerDialog tpDialog = new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));

		//For some reason the only way to intercept "cancel" button is this way.  Android limitation.
		tpDialog.setButton(DialogInterface.BUTTON_NEGATIVE, ("cancel"), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_NEGATIVE) {
					cancelAlarm();
				}
				Toast.makeText(getActivity().getApplicationContext(), "CLicked " + which, Toast.LENGTH_SHORT).show();
			}
		});

		return tpDialog;
	}

	public void onTimeSet(TimePicker view, int hourOfDaySel, int minuteSel) {
		// Do something with the time chosen by the user
		TextView tv = (TextView)(getActivity().findViewById(R.id.text_time_daily));
		String tod = (hourOfDaySel > 12) ? " PM":" AM";
		String min = (minuteSel < 10) ? ("0") : "";
		min += minuteSel;
		tv.setText("  " + (hourOfDaySel%12)+":"+min+ tod);

		final Calendar now = Calendar.getInstance();
		
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hourOfDaySel);
		calendar.set(Calendar.MINUTE, minuteSel);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0); 
		
		if(calendar.before(now))
			calendar.add(Calendar.DAY_OF_YEAR, 1); //Don't trigger if time is earlier in the day!

		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);

	}

	//Clean up when cancelled
	public void cancelAlarm()
	{
		if(am!=null)
			am.cancel(pi);

		if(tv!=null)
			tv.setText("");


		if(checkBox != null)
			checkBox.setChecked(false);
	}
}