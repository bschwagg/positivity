package com.bschwagler.positivity;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseUser;

import android.app.Application;
import android.util.Log;

public class MainApplication extends Application {

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
		ParseUser.getCurrentUser().saveInBackground(); 
		ParseACL defaultACL = new ParseACL();
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);

		ParseACL.setDefaultACL(defaultACL, true);

		

	}

	protected void initSingletons()
	{
		// Initialize the instance of MySingleton
		GlobalsAreBad.getInstance();
	}
}
