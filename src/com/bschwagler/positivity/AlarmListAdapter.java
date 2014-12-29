/*+----------------------------------------------------------------------
 ||
 ||  Class com.bschwagler.positivity.adapter 
 ||
 ||        Author:  Brad  Dec 21, 2014
 ||
 ||        Description:  
 ||                  
 ||
  +---------------------------------------------------------------------- */
package com.bschwagler.positivity;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ActionBar.LayoutParams;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 21, 2014
 *
 * Class AlarmListAdapter.java Description: 
 *
 *	Holds a list of alarm entries.  Each has a delete or add button to 
 *  
 */
public class AlarmListAdapter extends BaseAdapter implements ListAdapter { 
	private Context context; 

	private AlarmListAdapter ala;

	private int itemHeight;
	private int margin;

	public AlarmListAdapter( Context context) { 
		this.context = context; 
		ala = this;
		int currentapiVersion = android.os.Build.VERSION.SDK_INT;
		if (currentapiVersion >= android.os.Build.VERSION_CODES.LOLLIPOP){
		    // Do something for above versions
			margin = 180;
			itemHeight = 110;
		} else{
		    // do something for phones running an SDK before
			margin = 60;
			itemHeight = 35;
		}
	} 

	@Override
	public int getCount() { 
		return Globals.getInstance().dailyAlarmList.size(); 
	} 

	@Override
	public Object getItem(int pos) { 
		return Globals.getInstance().dailyAlarmList.get(pos); 
	} 

	@Override
	public long getItemId(int pos) { 
		return Globals.getInstance().dailyAlarmList.get(pos).getTimeInMillis();
		//just return 0 if your list items do not have an Id variable.
	} 

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (view == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			view = inflater.inflate(R.layout.alarm_list_layout, null);
		} 


		Button deleteBtn = (Button)view.findViewById(R.id.delete_btn);
		Button addBtn = (Button)view.findViewById(R.id.add_btn);
		Button addRndBtn = (Button)view.findViewById(R.id.add_rnd_btn);

		//Handle TextView and display string from your list
		TextView listItemText = (TextView)view.findViewById(R.id.list_item_string); 
		String atime = "";
		if( Globals.getInstance().dailyAlarmList.get(position).getTimeInMillis() != 0) {
			SimpleDateFormat sdf = new SimpleDateFormat("h:mm a");
			atime = sdf.format(Globals.getInstance().dailyAlarmList.get(position).getTime() );
			deleteBtn.setVisibility(Button.VISIBLE);
			addBtn.setVisibility(Button.INVISIBLE);
			addRndBtn.setVisibility(Button.INVISIBLE);
		} else {
			deleteBtn.setVisibility(Button.INVISIBLE);
			addBtn.setVisibility(Button.VISIBLE);
			addRndBtn.setVisibility(Button.VISIBLE);
		}
		listItemText.setText( atime ); 


		//Handle buttons and add onClickListeners
		deleteBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) { 
				//do something
				long t = Globals.getInstance().dailyAlarmList.get(position).getTimeInMillis();
				Globals.getInstance().dailyAlarmList.remove(position);
				update();
				//Remove the alarm which will fire off our AlarmReceiver..
				Intent intent = new Intent(context /*MainActivity.this*/, AlarmReceiver.class);
				PendingIntent pi = PendingIntent.getBroadcast( context /*MainActivity.this*/, (int) t, intent, /*PendingIntent.FLAG_UPDATE_CURRENT)*/	0 );
				AlarmManager am = (AlarmManager)(context.getSystemService( Context.ALARM_SERVICE ));
				am.cancel(pi);	
				
				//Show the hint if applicable
				View hint = (View)((Activity)context).findViewById(R.id.hint_popup);
				if(Globals.getInstance().dailyAlarmList.size() < 2 )
					hint.setVisibility(View.VISIBLE);
			}
		});
		addBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) { 
				final TimePickerFragment newFragment = new TimePickerFragment(ala);	//Create this only once and it will remember the last open settings  
				newFragment.show( ((Activity)context).getFragmentManager(), "timePicker");   
				//remove the hint if applicable
				View hint = (View)((Activity)context).findViewById(R.id.hint_popup);
				hint.setVisibility(View.GONE);
				hint.invalidate(); //redraw for droid 4.4
			}
		});
		addRndBtn.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v) { 
				setRandomAlarms();
				//remove the hint if applicable
				View hint = (View)((Activity)context).findViewById(R.id.hint_popup);
				hint.setVisibility(View.GONE);
				hint.invalidate();
			}
		});

		return view; 
	} 

	public void update()
	{
		ListView lView = (ListView)((Activity)context).findViewById(R.id.alarm_listview);
		if(lView != null){
			RelativeLayout.LayoutParams rlo = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, (Globals.getInstance().dailyAlarmList.size()+1)*itemHeight);
			rlo.topMargin = margin;
			lView.setLayoutParams(rlo);
		}
		notifyDataSetChanged();
	}


	private void setRandomAlarms()
	{

		int hour =(int) (Math.random() * 14.0f + 7.0f);//Assumption: Between 7am and 10pm
		int minute =(int) (Math.random() * 59.0f); //Any minute
		int second =(int) (Math.random() * 59.0f); //Any second
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, hour);
		calendar.set(Calendar.MINUTE, minute);
		calendar.set(Calendar.SECOND, second);
		calendar.set(Calendar.MILLISECOND, 0); 

		Calendar now = Calendar.getInstance();
		if(calendar.before(now))
			calendar.add(Calendar.DAY_OF_YEAR, 1); //Don't trigger if time is earlier in the day!

		Globals.getInstance().dailyAlarmList.add( calendar );
		update(); //refresh view

		//alarm stuff
		PendingIntent pi;
		AlarmManager am;
		pi = PendingIntent.getBroadcast( ((Activity)context).getApplication(), (int)calendar.getTimeInMillis() , new Intent("com.bschwagler.wakeup"), 0 );
		am = (AlarmManager)(((Activity)context).getApplicationContext().getSystemService( Context.ALARM_SERVICE ));
		am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pi);
	}


}