package com.bschwagler.positivity;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class PositivityService.java Description: 
 *
 *  TODO: any long running service lives here, such as ensuring the application is running daily
 */
public class PositivityService extends IntentService {

	public PositivityService()
	{
		super("");
	}
	/**
	 * @param name
	 */
	public PositivityService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent workIntent) {
		// Gets data from the incoming Intent
		String dataString = workIntent.getDataString();
		Toast.makeText(this.getApplicationContext(), "Service got Intent! " + dataString, Toast.LENGTH_SHORT).show();
	}


}
