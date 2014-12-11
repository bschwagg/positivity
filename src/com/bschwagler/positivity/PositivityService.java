package com.bschwagler.positivity;

import android.app.IntentService;
import android.content.Intent;
import android.widget.Toast;

public class PositivityService extends IntentService {

	@Override
	protected void onHandleIntent(Intent workIntent) {
		// Gets data from the incoming Intent
		String dataString = workIntent.getDataString();
		Toast.makeText(this.getApplicationContext(), "Service got Intent! " + dataString, Toast.LENGTH_SHORT).show();
	}

	public PositivityService(String name) {
		super(name);
		// TODO Auto-generated constructor stub
	}

}
