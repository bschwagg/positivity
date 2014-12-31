package com.bschwagler.positivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.PushService;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;


/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class MainApplication.java Description: 
 * Application to start activities. Used to initialize objects which need to be created only once
 *
 *  
 */
public class MainApplication extends Application {
	public List<ParseObject> leaderBoard;
	public ParseObject myParseObject;
	@Override
	public void onCreate()
	{
		super.onCreate();

		// Initialize the singletons so their instances
		// are bound to the application process.
		initSingletons();
		initParse();
	}

	private void initParse() {

		Log.d("cloud", "Initializing PARSE");
		Parse.enableLocalDatastore(this);
		// Initialize for our Dashboard ID..
		Parse.initialize(this, "EYc7GORw58kM2wMByUVqBYR9uPulnCKXIsfDYEmB", "f6Ftl4WgTSC3NKWXbVaHkEolA9skWL9m692LeyEj");


		ParseUser.enableAutomaticUser();
		//ParseInstallation.getCurrentInstallation().saveInBackground(); //lets PUSH requests go through
		//ParseUser.getCurrentUser().saveInBackground(); 
		ParsePush.subscribeInBackground("", new SaveCallback() {
			@Override
			public void done(ParseException e) {
				if (e == null) {
					Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
				} else {
					Log.e("com.parse.push", "failed to subscribe for push", e);
				}
			}
		});
		
		//PushService.setDefaultPushCallback(this,  BackgroundActivity.class);
		//ParseInstallation.getCurrentInstallation().saveInBackground();  
		
		ParseACL defaultACL = new ParseACL();
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		leaderBoard = new ArrayList<ParseObject>();
		myParseObject = null;

	}

	protected void initSingletons()
	{
		// Initialize the instance of MySingleton
		Globals.reloadInstance(this);

		//always have a dummy entry in the list where all we can do is add
		if(Globals.getInstance().dailyAlarmList.size() == 0){
			Calendar dummy = Calendar.getInstance();
			dummy.setTimeInMillis( 0 );
			Globals.getInstance().dailyAlarmList.add(  dummy );
		}
	}
}
