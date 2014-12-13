package com.bschwagler.positivity;

//TODO Public settings. 
//There's got to be a better way to do global variables? 

public class GlobalsAreBad {
    private static GlobalsAreBad mInstance= null;

    public boolean vibEnabled;
    public boolean noiseEnabled;

    protected GlobalsAreBad(){}

    public static synchronized GlobalsAreBad getInstance(){
    	if(null == mInstance){
    		mInstance = new GlobalsAreBad();
    	}
    	return mInstance;
    }
}