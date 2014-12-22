package com.bschwagler.positivity.adapter;

import java.util.ArrayList;
import java.util.Calendar;

import android.app.ActionBar.LayoutParams;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bschwagler.positivity.AlarmListAdapter;
import com.bschwagler.positivity.Globals;
import com.bschwagler.positivity.R;
import com.bschwagler.positivity.TimePickerFragment;

/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class SettingsFragment.java Description: 
 *
 * Manages UI for the settings page.
 *  
 */
public class SettingsFragment extends Fragment {

	private  AlarmListAdapter adapter;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState); //save the settings!
		
		final View rootView = inflater.inflate(R.layout.settings, container, false);

	    //instantiate custom adapter
	    adapter = new AlarmListAdapter(getActivity());

	    //handle listview and assign adapter
	    ListView lView = (ListView)rootView.findViewById(R.id.alarm_listview); 
	    lView.setAdapter(adapter);
	    adapter.update();
	    //TODO: make the list height size dynamic. How?
	    
		SetupWakeAlarm(rootView);
		SetupMinsAlarm(rootView, inflater);
		SetupLocationAlarm(rootView);
		setupParams(rootView);
		
		return rootView;
	}
	

	private void SetupLocationAlarm(View rootView) {
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_location);
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//is  checked?
				if (((CheckBox) v).isChecked()) {
					Toast.makeText(getActivity().getApplicationContext(), "Location alarm not yet working!", Toast.LENGTH_SHORT).show();            
				}
				else {

				}
			}
		});
	}

	/**
	 * Setup how many minutes using the phone until a message notice is displayed.
	 * Also default from the global settings which are stored.
	 * @param rootView
	 * @param inflater
	 */
	private void SetupMinsAlarm(View rootView, final LayoutInflater inflater) {
		final CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_minutes);
		checkBox.setChecked( Globals.getInstance().phoneUseAlarm ); //default

		
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
				Globals.getInstance().phoneUseAlarm = checkBox.isChecked();
				
				//is  checked?
				if (((CheckBox) v).isChecked()) {
					
					View npView = inflater.inflate(R.layout.number_picker, null);
					final NumberPicker np = (NumberPicker) npView.findViewById(R.id.number_picker);
					np.setMaxValue(30);
					np.setMinValue(1);
					np.setWrapSelectorWheel(false);
					np.setValue(5);
					AlertDialog ad = new AlertDialog.Builder(getActivity())
					.setTitle("How many minutes of phone use before showing a message:")
					.setView(npView)
					.setPositiveButton("ok",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Globals.getInstance().phoneUseAlarmMinutes = np.getValue();
							checkBox.setText("After " + Integer.toString( np.getValue() ) + " minutes using phone");
						}
					})
					.setNegativeButton("cancel",
							new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							Globals.getInstance().phoneUseAlarm = false;
							checkBox.setChecked(false);
							checkBox.setText("After minutes using phone");
						}
					})
					.create();
					ad.show();             
				} else {
					checkBox.setText("After minutes using phone");
				}
			}
		});
	}

	/**
	 * Set the option to show a notice when you wake up. Default from global settings.
	 * @param rootView
	 */
	private void SetupWakeAlarm(View rootView) {
			
		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_wakeup);
		checkBox.setChecked( Globals.getInstance().firstWakeAlarm ); //defaulto
		checkBox.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//is  checked?
				Globals.getInstance().firstWakeAlarm = ((CheckBox) v).isChecked();
			}
		});
	}

	/**
	 * Setup msg notification parameters and default the checkboxes from our global settings
	 * @param rootView
	 */
	private void setupParams(View rootView)
	{	
		CheckBox checkBoxVib = (CheckBox) rootView.findViewById(R.id.setting_vibrate);
		checkBoxVib.setChecked( Globals.getInstance().vibEnabled  );
		checkBoxVib.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Globals.getInstance().vibEnabled = isChecked;
			}
		});

		CheckBox checkBoxAudio = (CheckBox) rootView.findViewById(R.id.setting_audio);
		checkBoxAudio.setChecked( Globals.getInstance().noiseEnabled );
		checkBoxAudio.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Globals.getInstance().noiseEnabled = isChecked;
			}
		});
		
		CheckBox checkBoxCoutdown = (CheckBox) rootView.findViewById(R.id.setting_countdown);
		checkBoxCoutdown.setChecked( Globals.getInstance().useCountdown );
		checkBoxCoutdown.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Globals.getInstance().useCountdown = isChecked;
			}
		});
	}


}
