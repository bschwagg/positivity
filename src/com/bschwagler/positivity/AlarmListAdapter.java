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
import android.graphics.Point;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.WindowManager;
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

		//Default view for the hint
		View hint = (View)((Activity)context).findViewById(R.id.hint_popup);
		if(Globals.getInstance().dailyAlarmList.size() < 2 )
			hint.setVisibility(View.VISIBLE);
		else
			hint.setVisibility(View.GONE);

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
		WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		Display display = wm.getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		int height = size.y;

		float marginFactor = 9.2f;
		// Nexus5 with 1776 height
		// margin = 180; for 1776 height  factor=9.8
		// itemHeight = 110; for 1776 height factor=16.2

		//nexus 4 1184 height
		//same factors as above works fine

		// Crappy phone with 284px height
		// margin = 60; for 284  factor 4.7
		// itemHeight = 35; for 284 factor 8.1
		if(height < 400) //margin is bigger on smaller displays due to menu headings
			marginFactor = 5.0f;

		itemHeight = (int)((float)height/15.5f);
		margin = (int)((float)height/marginFactor);

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