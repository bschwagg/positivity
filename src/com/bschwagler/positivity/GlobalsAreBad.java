package com.bschwagler.positivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import android.content.SharedPreferences;

import com.parse.ParseObject;

//TODO Public settings. 
//There's got to be a better way to do global variables? 

public class GlobalsAreBad {
    private static GlobalsAreBad mInstance= null;

    public boolean vibEnabled;
    public boolean noiseEnabled;
    public List<ParseObject> leaderBoard;
	public boolean useCountdown;
	public ParseObject myParseObject;
	public boolean firstWakeAlarm;
	public boolean phoneUseAlarm;
	public int phoneUseAlarmMinutes;

    protected GlobalsAreBad(){
    	leaderBoard = new ArrayList<ParseObject>();
    	myParseObject = null;
    }

    public static synchronized GlobalsAreBad getInstance(){
    	if(null == mInstance){
    		mInstance = new GlobalsAreBad();
    	}
    	return mInstance;
    }
}