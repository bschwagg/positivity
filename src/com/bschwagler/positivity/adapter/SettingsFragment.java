package com.bschwagler.positivity.adapter;

import com.bschwagler.positivity.R;
import com.bschwagler.positivity.TimePickerFragment;

import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextClock;
import android.widget.TextView;

public class SettingsFragment extends Fragment {

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		final View rootView = inflater.inflate(R.layout.settings, container, false);

		CheckBox checkBox = (CheckBox) rootView.findViewById(R.id.alarm_time);
		checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

		       @Override
		       public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
		    	   TimePickerFragment newFragment = new TimePickerFragment();
		    	   TextView tv = (TextView)(rootView.findViewById(R.id.text_time));
		    	   if(isChecked){
		    		    newFragment.show(getActivity().getFragmentManager(), "timePicker");
		    	   } else {
		    		   newFragment.cancelAlarm();
		    		   tv.setVisibility( View.INVISIBLE );
		    	   }
		       }
		   }
		);     
		return rootView;
	}
	
}
