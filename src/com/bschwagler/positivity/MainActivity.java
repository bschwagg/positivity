package com.bschwagler.positivity;

import java.io.IOException;
import java.util.List;

import com.bschwagler.positivity.adapter.SocialFragment;
import com.bschwagler.positivity.adapter.TabsPagerAdapter;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Application;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class MainActivity.java Description: 
 *  This activity is in charge of creating the settings tabs, leader board and dialog windows, 
 *	scheduling the timer with a (always on) background service, and managing
 *	the phrases to be displayed.  
 *  
 */
public class MainActivity extends FragmentActivity implements ActionBar.TabListener , SocialFragment.OnCompleteListener {

	//Stuff for pages and tabs
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	SocialFragment socFrag;
	private MainActivity activity;
	
	// Tab titles
	private String[] tabs = { "Welcome", "Settings", "Social" };
	private int msgCount = 0;

	//Stuff for alarm and timer dialog
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;
	private boolean querryRunning = false;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		setupTabs(savedInstanceState);
		setupAlarmReceiver();
		promptUserName(this); //all that's stored locally is the user name. 
		activity = this;
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		Globals.getInstance().saveToFile(this);
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		//Couldn't get the fragment to automatically save. So this is brute force
		Globals.getInstance().saveToFile(this);
		
//		if(pi != null && am != null)
		//			am.cancel(pi);
		//		if(br != null)
		//		unregisterReceiver(br);
	}
	

	/**
	 * Starts the process of requesting leaderboard data and when it arrives updates appropriate widgets
	 */
	@SuppressWarnings("deprecation")
	private void updateCloudLeaderBoard() {

		ParseAnalytics.trackAppOpened(getIntent());

		//Get the user's data here
		final SharedPreferences settings = this.getSharedPreferences("UserData", 0);
		final String name = settings.getString("username", "");

		//Get ALL the leader board data..
		if( querryRunning ) {
			Log.d("cloud", "Still waiting on prior query to finish..");
		} else {
			Log.d("cloud", "Starting leader board download");
			ParseQuery<ParseObject> queryLB = ParseQuery.getQuery("Entry");
			queryLB.whereExists("username");
			queryLB.orderByDescending("points");
			queryLB.findInBackground(new FindCallback<ParseObject>() {
				public void done(List<ParseObject> scoreList, ParseException e) {
					if (e == null) {
						Log.d("cloud", "Retrieved " + scoreList.size() + " scores");
						((MainApplication)getApplication()).leaderBoard.clear();
						((MainApplication)getApplication()).leaderBoard.addAll(scoreList); //save to our leader board!
						
						//TEST: print the leaderboard
						for(ParseObject p : scoreList) {
							//Log.d("cloud", "Cloud Info: Name: " + p.getString("username") + " Score: " + p.getInt("points") + " Countdown: " + p.getBoolean("countdown")); //TEST
							if(p.getString("username").equals((name))){
								((MainApplication)getApplication()).myParseObject = p;
								Log.d("cloud", "Found myself in the cloud");
							}
						}
					
						updateLeaderBoard();
					} else {
						Log.d("cloud", "Error: Unable to download leader board from cloud.  Error:" + e.getMessage());
					}
					querryRunning = false;
				}
			});
			querryRunning  = true;
		}
	}

	/**
	 * Creates a new user entry in the cloud. Should be called only once
	 * @param name User's name
	 */
	private void createNewCloudEntry(String name) {
		ParseObject score = new ParseObject("Entry");
		score.put("points",  0); //Reset score to nill!
		score.put("username",  name);
		score.put("countdown",  Globals.getInstance().useCountdown ); 
		score.saveInBackground();
		Log.d("cloud","Storing user entry ");

	}
	private void promptUserName(final Activity act) {

		final SharedPreferences settings = this.getSharedPreferences("UserData", 0);
		String name = settings.getString("username", "");

		//Do we need to show?
		if(  name == "") {
			final EditText txtUrl = new EditText(this);
			txtUrl.setHint("");
			new AlertDialog.Builder(this)
			.setTitle("Welcome to Positivity!")
			.setMessage("Please enter your name:")
			.setView(txtUrl)
			.setPositiveButton("Let's go!",  new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					final String desiredUser = txtUrl.getText().toString();

					ParseQuery<ParseObject> query = ParseQuery.getQuery("Entry");
					query.whereEqualTo("username", desiredUser);
					query.findInBackground(new FindCallback<ParseObject>() {
						public void done(List<ParseObject> entryList, ParseException e) {
							if (e != null ) {
								Log.d("cloud", "Error getting user info. Error:" + e.getMessage());
								Toast.makeText(act, "Sorry! Unable to check if this name is already taken online. Please restart to try again! ", Toast.LENGTH_SHORT).show(); //TEST
								//don't get into an endless loop prompting user name!
							} else if(entryList.size() == 0) {
								Log.d("cloud", "User name not taken! Creating a new entry. ");
								SharedPreferences.Editor editor = settings.edit();
								editor.putString("username", desiredUser);
								editor.commit();
								Toast.makeText(act, "Thanks " + desiredUser + "!", Toast.LENGTH_SHORT).show(); //TEST
								createNewCloudEntry(desiredUser); //If this fails we should try again...
							} 	else  {
								//Check that user is unique here!
								Log.d("cloud", "Retrieved " + entryList.size() + " scores");
								Toast.makeText(act, "Sorry, that name is already taken! Please try again...", Toast.LENGTH_SHORT).show(); //TEST
								promptUserName(act); //recursion is good! or not...
							}
						}
					});
				}
			})
			.setNegativeButton("Later",  new DialogInterface.OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					Toast.makeText(act, "No problem.. we can do that later.. Enjoy!", Toast.LENGTH_SHORT).show(); //TEST
				}
			})
			.show();
		} else {
			viewPager.setCurrentItem( 2 ); //Jump to the social leaderboard if we've already registered
		}

	}

	// Initialize tabs
	@SuppressWarnings("deprecation")
	private void setupTabs(Bundle savedInstanceState)
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
		

		socFrag = (SocialFragment) mAdapter.getFragment(2);
		

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
				//cloud storage for the leader board. Stored locally until updated.  This keeps the network traffic fairly low
				Log.d("main", "On page " + Integer.toString(position));
				if(position==2)
					updateCloudLeaderBoard();  //Begin to refresh the stats!
				}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
	}


	/**
	 * This is the receiver to handle alarm events and update whatever widgets required
	 */
	private void setupAlarmReceiver() {

		//Setup callback for the alarm
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {

				//Do we have any special messages to handle?
				Bundle extras = i.getExtras();
				if(extras != null){
					//Here we want to show the dialog immediately
					if(i.getStringExtra("immediate") != null){
						//pop up the dialog right away
						Intent bgIntent = new Intent(c, BackgroundActivity.class);
						startActivity( bgIntent );

						//Here we want to pop up a toast message
						String msg = i.getStringExtra("toastMsg");
						if(msg != null)
							Toast.makeText(c, msg, Toast.LENGTH_SHORT).show();       
						return;
					}
				}
				Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_128);

				NotificationCompat.Builder mBuilder =
						new NotificationCompat.Builder(c)

				.setSmallIcon(R.drawable.ic_launcher_24)
				.setLargeIcon(bm)
				//				.setContentText("Hello World!");
				.setContentTitle("Positivity");

				// Creates an explicit intent for an Activity in your app
				Intent resultIntent = new Intent(c, BackgroundActivity.class);

				// Sets the Activity to start in a new, empty task
				resultIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
				resultIntent.putExtra("notificationId", msgCount); //store ID so we can cancel the notif

				PendingIntent resultPendingIntent =
						PendingIntent.getActivity(
								c,
								0,
								resultIntent,
								PendingIntent.FLAG_UPDATE_CURRENT);

				mBuilder.setContentIntent(resultPendingIntent);
				//pop the notification in heads up on top of screen, similar to incoming calls
				//TODO: may have to setup ringtone or vibration to make this work as well
				mBuilder.setPriority(NotificationCompat.PRIORITY_MAX); 
				//mBuilder.setVisibility(Notification.VISIBILITY_PUBLIC); //android 5.0 lock screen feature
				NotificationManager mNotificationManager =
						(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// mId allows you to update the notification later on.
				mNotificationManager.notify(msgCount, mBuilder.build());

				doNotifFeedback();

				msgCount++; //Essentially a UID
				//				Toast.makeText(c, "Notification " + msgCount + " sent", Toast.LENGTH_SHORT).show(); //TEST
				//The notification will then start a special service. The service is detached 
				//see: https://developer.android.com/guide/topics/ui/notifiers/notifications.html
			}


		};
		registerReceiver(br, new IntentFilter("com.bschwagler.wakeup") );

	}


	/**
	 *  Activates feedback for vibration and audio alarms, depending on global settings
	 */
	private void doNotifFeedback() {
		// Vibrate the mobile phone
		if(Globals.getInstance().vibEnabled) {
			Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
			if(vibrator != null)
				vibrator.vibrate(800);
		}

		if(Globals.getInstance().noiseEnabled) {
			try {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION /*TYPE_ALARM*/);
				Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
				r.play();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	@Override
	public void onTabSelected(@SuppressWarnings("deprecation") Tab tab, FragmentTransaction ft) {

		// show respected fragment view
		viewPager.setCurrentItem(tab.getPosition());


	}

	/**
	 * Signal to the leaderboard fragment we have new data to update
	 */
	private void updateLeaderBoard() {	
		
		socFrag = (SocialFragment) mAdapter.getFragment(2);
		if(socFrag != null) {
			socFrag.update();
		} else {
			Log.d("cloud", "Leaderboard unable to refresh due to social frag not loaded");
		}
	}

	
	/**
	 *  Handle the test button click. This callback is set from the layout, so we can access it
	 * @param v
	 */
	public void myButtonClickHandler(View v) 
	{
		final boolean useNotif = false;
		if(useNotif) {
			//Test the dialog box in .5 seconds
			pi = PendingIntent.getBroadcast( this, 0, new Intent("com.bschwagler.wakeup"),
					0 );
			am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
			am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 500 /*ms*/, pi );
		} else {
			//Create an intent to open up the background activity, which contains the msg dialog
			doNotifFeedback();
			Intent bgIntent = new Intent(this, BackgroundActivity.class);
			startActivity( bgIntent );
		}
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {


	}

	@Override
	public void onTabReselected(Tab tab, FragmentTransaction ft) {

	}

	/* (non-Javadoc)
	 * @see com.bschwagler.positivity.adapter.SocialFragment.OnCompleteListener#onComplete()
	 */
	@Override
	public void onComplete() {
		Log.d("main", "Just got notification that the social tab is ready for updating");
		updateLeaderBoard();	
		
	}


}