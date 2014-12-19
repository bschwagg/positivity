package com.bschwagler.positivity;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.parse.ParseObject;

// Public singleton container to hold persistent app settings 

public class Globals implements Serializable {
	// singleton accessor
	public static synchronized Globals getInstance( ){
		if(null == mInstance){
				mInstance = new Globals();
		}
		return mInstance;
	}
	
	//Initialization required with a Context to reload data
	//Call this to restore the instance from file
	public static synchronized Globals reloadInstance(Context c ){
		if(null == mInstance){
			if( !restoreFromFile(c) )
				mInstance = new Globals();
		}
		return mInstance;
	}
	
	private static final long serialVersionUID = 1L;

	private static Globals mInstance= null;

	public boolean vibEnabled = true;
	public boolean noiseEnabled;
	public boolean useCountdown = true;
	public boolean firstWakeAlarm = true;
	public boolean phoneUseAlarm;
	public int phoneUseAlarmMinutes;

	public int numRandAlarms = 3;

	public ArrayList<Calendar> dailyAlarmList;

	protected Globals(){
		
		dailyAlarmList = new ArrayList<Calendar>();
	}

	public void saveToFile(Context c)
	{
		FileOutputStream fos;
		try {
			fos = c.openFileOutput("positivity", Context.MODE_PRIVATE);
			ObjectOutputStream os = new ObjectOutputStream(fos);
			os.writeObject(this);
			os.close();
			Log.d("Error", "Saved settings data to file");
		} catch ( IOException e ) {
			Log.d("Error", "Unable to save globals");
			e.printStackTrace();
		}
	}

	private static boolean restoreFromFile(Context c)
	{
		FileInputStream fis;
		ObjectInputStream is;
		try {
			fis = c.openFileInput("positivity");
			try {
				is = new ObjectInputStream(fis);
				mInstance = (Globals) is.readObject();
				is.close();
				Log.d("Error", "Restored global settings!");
				return true;
			} catch (IOException | ClassNotFoundException e) {
				Log.d("Error", "Unable to restore globals");
				e.printStackTrace();
				mInstance = null;
			}
		} catch (FileNotFoundException e1) {
			Log.d("Error", "Globals file not found");
		}

		return false;
	}

}