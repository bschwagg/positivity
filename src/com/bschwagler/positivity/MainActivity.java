package com.bschwagler.positivity;

import com.bschwagler.positivity.adapter.TabsPagerAdapter;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.AlarmManager;
import android.app.FragmentTransaction;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Toast;


/**
 * @author Brad
 *
 *	This activity is in charge of creating the settings, leader board and dialog windows, 
 *	scheduling the timer with a (always on) background service, and managing
 *	the phrases to be displayed.  It will eventually synchronize events to 
 *	the cloud so a leader board can be displayed.
 *
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener  {
	
	//Stuff for pages and tabs
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Welcome", "Settings", "Stats" /*TBD: leader board?*/ };

	//Stuff for alarm and timer dialog
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;

	//Stuff for message dialog box
	Intent bgIntent;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		
		setupTabs();
		setupAlarmReciever();
	}
	
	// Initialize tabs
	private void setupTabs()
	{
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);        

		// Adding Tabs
		for (String tab_name : tabs) {
			actionBar.addTab(actionBar.newTab().setText(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}


	private void setupAlarmReciever() {
		 
		//Create an intent to open up the background activity, which contains the msg dialog
		bgIntent = new Intent(this, BackgroundActivity.class);
		 
		//Setup callback for the alarm
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				startActivity( bgIntent ); //kick off the background activity
			}
		};
		registerReceiver(br, new IntentFilter("com.bschwagler.wakeup") );
		
	}


	@Override
	protected void onDestroy() {
		am.cancel(pi);
		unregisterReceiver(br);
		super.onDestroy();
	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		//Launch a timer example when the "Stats" tab is selected
		if(tab.getText() == "Stats" && am != null) {
			Toast.makeText(this.getApplicationContext(), "Come on back! Not yet implemented...", Toast.LENGTH_SHORT).show();
		}

		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());
	}

	// Handle the button click. This callback is set from the layout, so we can access it
	public void myButtonClickHandler(View v) 
	{
		//Test the dialog box in .5 seconds
		pi = PendingIntent.getBroadcast( this, 0, new Intent("com.bschwagler.wakeup"),
				0 );
		am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
		am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 500 /*ms*/, pi );
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {
		// TODO Auto-generated method stub

	}


}