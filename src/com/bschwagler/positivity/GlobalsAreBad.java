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

//TODO Public settings. 
//There's got to be a better way to do global variables? 

public class GlobalsAreBad implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static GlobalsAreBad mInstance= null;

	public boolean vibEnabled = true;
	public boolean noiseEnabled;
	public boolean useCountdown = true;
	public boolean firstWakeAlarm = true;
	public boolean phoneUseAlarm;
	public int phoneUseAlarmMinutes;

	public int numRandAlarms = 3;

	public ArrayList<Calendar> dailyAlarmList;

	public GlobalsAreBad(){
		
		dailyAlarmList = new ArrayList<Calendar>();
	}

	public static synchronized GlobalsAreBad getInstance( ){
		return mInstance;
	}
	
	public static synchronized GlobalsAreBad initializeInstance(Context c ){
		if(null == mInstance){
			if( !restoreFromFile(c) )
				mInstance = new GlobalsAreBad();
		}
		return mInstance;
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
		GlobalsAreBad simpleClass = null;
		try {
			fis = c.openFileInput("positivity");
			try {
				is = new ObjectInputStream(fis);
				mInstance = (GlobalsAreBad) is.readObject();
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