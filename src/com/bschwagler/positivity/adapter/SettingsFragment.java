package com.bschwagler.positivity.adapter;

import java.util.Calendar;

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


	 
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.settings, container, false);

		setupDailyAlarm(rootView);
		setupRandomAlarm(rootView, inflater);
		
		return rootView;
	}

	private void setupRandomAlarm(View rootView, final LayoutInflater inflater) {
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_random);

	
			
		
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
		    				                    }
		    				                })
		    				            .create();
		    				    ad.show();
		    	   } else {
		    		   
		    	   }
		       }
		   }
		);    
		
	}
	private void setRandomAlarms(int num) {
		//TODO: Make num alarms
		int hour =(int) (Math.random() * 24.0f);
		int minute =(int) (Math.random() * 59.0f);
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0); 
		//	 calendar.add(Calendar.DAY_OF_YEAR, 1);

		//alarm stuff
		PendingIntent pi;
		AlarmManager am;
		 pi = PendingIntent.getBroadcast( getActivity(), 2/*id*/, new Intent("com.bschwagler.wakeup"), 0 );
		 am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));
		 
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
		
		Toast.makeText(getActivity().getApplicationContext(), num + " alarms: But set one alarm for " + hour + ":" + minute, Toast.LENGTH_SHORT).show();
		
	}
	
	private void cancelRandomAlarms()
	{
		PendingIntent pi = PendingIntent.getBroadcast( getActivity(), 2/*id*/, new Intent("com.bschwagler.wakeup"), 0 );
		AlarmManager am = (AlarmManager)(getActivity().getSystemService( Context.ALARM_SERVICE ));
		am.cancel(pi);
	}
	
	private void setupDailyAlarm(View rootView) {
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_time);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

		       @Override
		       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		    	   TimePickerFragment newFragment = new TimePickerFragment();
		    	   if(isChecked){
		    		    newFragment.show(getActivity().getFragmentManager(), "timePicker");
		    	   } else {
		    		   TextView tv = (TextView)(getActivity().findViewById(R.id.text_time));
		    		   if(tv!=null)
		    			   tv.setVisibility(View.INVISIBLE);
		    		   newFragment.cancelAlarm();
		    	   }
		       }
		   }
		);    
	}
	
}
