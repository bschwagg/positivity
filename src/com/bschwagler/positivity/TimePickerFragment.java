package com.bschwagler.positivity;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

public class TimePickerFragment extends DialogFragment
implements TimePickerDialog.OnTimeSetListener {

	int hour, minute;
	PendingIntent pi;
	AlarmManager am;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the current time as the default values for the picker
		final Calendar c = Calendar.getInstance();
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		
		 pi = PendingIntent.getBroadcast( getActivity(), 1/*id*/, new Intent("com.brad.wakeup"), 0 );
		 am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));


		// Create a new instance of TimePickerDialog and return it
		return new TimePickerDialog(getActivity(), this, hour, minute,
				DateFormat.is24HourFormat(getActivity()));
	}

	public void onTimeSet(TimePicker view, int hourOfDaySel, int minuteSel) {
		// Do something with the time chosen by the user
		 TextView tv = (TextView)(getActivity().findViewById(R.id.text_time));
		 String tod = (hourOfDaySel > 12) ? " PM":" AM";
		 tv.setText((hourOfDaySel%12)+":"+minuteSel+ tod);
		 tv.setVisibility(View.VISIBLE);
		
		 Calendar calendar = Calendar.getInstance();
		 calendar.set(Calendar.HOUR_OF_DAY, hourOfDaySel);
		 calendar.set(Calendar.MINUTE, minuteSel);
		 calendar.set(Calendar.SECOND, 0);


		 am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
				 AlarmManager.INTERVAL_DAY, pi);

	}
	
	public void cancelAlarm()
	{
		if(am!=null)
			am.cancel(pi);
	}
}