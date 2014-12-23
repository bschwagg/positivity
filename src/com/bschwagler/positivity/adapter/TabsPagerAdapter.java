package com.bschwagler.positivity.adapter;



import java.lang.ref.WeakReference;
import java.util.Hashtable;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
 
/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class TabsPagerAdapter.java Description: 
 *
 *  Adapter to manage viewing of the three tab fragments
 *  
 */
public class TabsPagerAdapter extends FragmentPagerAdapter {
 
	protected Hashtable<Integer, WeakReference<Fragment>> fragmentReferences = new  Hashtable<Integer, WeakReference<Fragment>>();

	public TabsPagerAdapter(FragmentManager fm) {
        super(fm);
    }
    
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }
 
    @Override
    public Fragment getItem(int index) {
 
    	Fragment f = null;
        switch (index) {
        case 0:
            // Welcome fragment activity
            f = new WelcomeFragment();
            break;
        case 1:
            // Settings fragment activity
            f = new SettingsFragment();
            break;
        case 2:
            // Social fragment activity
            f = new SocialFragment();
            break;
        default:
        	Log.d("ERROR", "Bad item in pager!");
        	break;
        }
        
        //Remember a reference to our fragment. Use weak reference since
        //we want to manage the frags scope w/i android lifecycles
        if(f!=null)
        	fragmentReferences.put(index, new WeakReference<Fragment>(f));
 
        return f;
    }
    
    public Fragment getFragment(int fragmentId) {
        WeakReference<Fragment> ref = fragmentReferences.get(fragmentId);
        return ref == null ? null : ref.get();
    }
 
    @Override
    public int getCount() {
        // get item count - equal to number of tabs
        return 3;
    }
 
}