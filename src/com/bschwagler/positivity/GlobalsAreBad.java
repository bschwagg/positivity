package com.bschwagler.positivity;

import java.util.ArrayList;
import java.util.List;

import com.parse.ParseObject;

//TODO Public settings. 
//There's got to be a better way to do global variables? 

public class GlobalsAreBad {
    private static GlobalsAreBad mInstance= null;

    public boolean vibEnabled;
    public boolean noiseEnabled;
    public List<ParseObject> leaderBoard;

	public boolean useCountdown;

    protected GlobalsAreBad(){
    	leaderBoard = new ArrayList<ParseObject>();
    }

    public static synchronized GlobalsAreBad getInstance(){
    	if(null == mInstance){
    		mInstance = new GlobalsAreBad();
    	}
    	return mInstance;
    }
}