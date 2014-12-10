package com.bschwagler.positivity.adapter;

import java.util.Calendar;

import com.bschwagler.positivity.GlobalsAreBad;

import com.bschwagler.positivity.R;
import com.bschwagler.positivity.TimePickerFragment;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.TextClock;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsFragment extends Fragment {

	private TextView randText;
	private int numRandomAlarms;
	
	@Override
	public void onDestroy() {
		//Clean up nicely so we don't have outstanding timers
		cancelRandomAlarms();
		super.onDestroy();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.settings, container, false);

		setupDailyAlarm(rootView); //implementation nicely buried in subclass
		setupRandomAlarm(rootView, inflater); //implementation NOT nicely buried in subclass (TODO)
		
		setupParams(rootView);

		return rootView;
	}

	private void setupParams(View rootView)
	{	
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.setting_vibrate);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				GlobalsAreBad.getInstance().vibEnabled = isChecked;
			}
		});
	}
	
	private void setupRandomAlarm(View rootView, final LayoutInflater inflater) {
		final CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_random);
		randText = (TextView) rootView.findViewById(R.id.text_time_random);
		numRandomAlarms = 0; //default
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					View npView = inflater.inflate(R.layout.number_picker, null);
					final NumberPicker np = (NumberPicker) npView.findViewById(R.id.number_picker);
					np.setMaxValue(12);
					np.setMinValue(1);
					np.setWrapSelectorWheel(false);
					np.setValue(3);
					AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setTitle("Number of messages per day:")
					.setView(npView)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {

							setRandomAlarms(np.getValue());
						}


					})
					.setNegativeButton("cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							cancelRandomAlarms();
							checkBox.setChecked(false);
						}
					})
					.create();
					ad.show();
				} else {
					//box was unchecked..
					cancelRandomAlarms();
				}
			}
		});    
	}
	
	private void setRandomAlarms(int num) {
		numRandomAlarms = num;
		String msg = "Alarms: ";
		for(int i = 0; i < numRandomAlarms; i++) {
			int hour =(int) (Math.random() * 14.0f + 7.0f);//Assumption: Between 7am and 10pm
			int minute =(int) (Math.random() * 59.0f); //Any minute
			int second =(int) (Math.random() * 59.0f); //Any second
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, second);
			calendar.set(Calendar.MILLISECOND, 0); 
			//	 calendar.add(Calendar.DAY_OF_YEAR, 1);

			//alarm stuff
			PendingIntent pi;
			AlarmManager am;
			pi = PendingIntent.getBroadcast( getActivity().getApplication(), 10+i/*id*/, new Intent("com.bschwagler.wakeup"), 0 );
			am = (AlarmManager)(getActivity().getApplicationContext().getSystemService( Context.ALARM_SERVICE ));

			am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
			msg +=  (" " + hour + ":" + minute + " ");
		}
		randText.setText(" ("+num+" alarms)");
		randText.setVisibility(View.VISIBLE);

		//Test
		Toast.makeText(getActivity().getApplicationContext(), msg, Toast.LENGTH_SHORT).show();

	}

	private void cancelRandomAlarms()
	{
		for(int i = 0; i < numRandomAlarms; i++) {
			PendingIntent pi = PendingIntent.getBroadcast( getActivity().getApplicationContext(), 10+i/*id*/, new Intent("com.bschwagler.wakeup"), 0 );
			AlarmManager am = (AlarmManager)(getActivity().getApplicationContext().getSystemService( Context.ALARM_SERVICE ));
			am.cancel(pi);
		}
		randText.setVisibility(View.INVISIBLE);
	}

	private void setupDailyAlarm(View rootView) {
		final TimePickerFragment newFragment = new TimePickerFragment();	//Create this only once and it will remember the last open settings  
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_time);

		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
				if(isChecked){
					newFragment.show(getActivity().getFragmentManager(), "timePicker");
				} else {
					newFragment.cancelAlarm(); //recursive?
				}
			}
		});    
	}

}
