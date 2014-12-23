package com.bschwagler.positivity.adapter;

import java.util.ArrayList;
import java.util.List;

import com.bschwagler.positivity.Globals;
import com.bschwagler.positivity.MainApplication;
import com.bschwagler.positivity.R;
import com.parse.ParseObject;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;



/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class SocialFragment.java Description: 
 *
 * Manages leaderboard and other social info
 *  
 */
public class SocialFragment extends Fragment {

	View rootView;
	ListView listView;
	ArrayAdapter<Spanned> adapter;
	ArrayList<Spanned> list = new ArrayList<Spanned>(); //Use HTML for the string, ie "Spanned"
	Activity act;
	int highlightIndex = -1;

	
	public static interface OnCompleteListener {
	    public abstract void onComplete();
	}

	private OnCompleteListener mListener;

	public void onAttach(Activity activity) {
	    super.onAttach(activity);
	    try {
	        this.mListener = (OnCompleteListener)activity;
	    }
	    catch (final ClassCastException e) {
	        throw new ClassCastException(activity.toString() + " must implement OnCompleteListener");
	    }
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.social, container, false);

		// Get ListView object from xml
		listView = (ListView) rootView.findViewById(R.id.list_leaderbaord);

		 
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		adapter = new ArrayAdapter<Spanned>(getActivity(),
				android.R.layout.simple_list_item_1, android.R.id.text1, list);


		// Assign adapter to ListView
		listView.setAdapter(adapter); 
		listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		act = getActivity();
		/*//In case we want to click on the user's score and get more info...
        // ListView Item Click Listener
        listView.setOnItemClickListener(new OnItemClickListener() {

              @Override
              public void onItemClick(AdapterView<?> parent, View view,
                 int position, long id) {

               // ListView Clicked item index
               int itemPosition     = position;

               // ListView Clicked item value
               String  itemValue    = (String) listView.getItemAtPosition(position);

                // Show Alert 
                Toast.makeText(getApplicationContext(),
                  "Position :"+itemPosition+"  ListItem : " +itemValue , Toast.LENGTH_LONG)
                  .show();

              }

         }); 
		 */
		
		return rootView;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("social", "About to signal the social frag is loaded");
		mListener.onComplete();
	}
	
	@Override
	public void onResume() {
		super.onResume();
//		listView.smoothScrollToPosition(highlightIndex);
	}
	
	public String getNiceQuote() {
		String[] q = {"Nice!","Keep it up!","Good job!","You're on your way!","Looking good!","Keep with it","Keep on going", "Happiness is on the rise","Success is in continuity",
				"Lah-di-dah","Rock on","Awesome","Keep on climbin' up","Great work","Oh yeah","Very nice","Nice!","booyah"};
		return q[ (int)(Math.random() * (double)q.length) ];
	}

	public void update() {
		// Add a point to our score!
		if(act != null)
		{
			SharedPreferences settings = act.getSharedPreferences("UserData", 0);
			String userName = settings.getString("username", "");
			
			
			if(adapter != null && list != null) {
				
				List<ParseObject> lb = ((MainApplication)getActivity().getApplication()).leaderBoard;
				//Not yet loaded?
				if(lb.size() == 0) {
					Log.d("cloud", "Leaderboard can't update yet.. no data..");
				}
				else
				{
					//populate the list!
					ProgressBar spinner = (ProgressBar) rootView.findViewById(R.id.loading_leaderboard);
					if(spinner != null)
						spinner.setVisibility(ProgressBar.INVISIBLE);
					list.clear();
					int i = 0;
					for(ParseObject p : lb){
						i++;
						String entry = "#" + i + " " +  p.getString("username") + " " + p.getInt("points")+ " pts";
						if(userName != "" && userName.equals(p.getString("username"))){
							highlightIndex = i;
							Log.d("cloud", "Found " + p.getString("username") + " at index " + Integer.toString(i));
							entry = "<strong style='font-size:200%'>"+entry + "</strong><br><i style='padding-left:0 8px'>&ensp&ensp&ensp ";
							if(p.getInt("points")==0)
								entry += "Click 'Add New Time' from the settings menu. Then you can start getting points to track your progress!";
							else
								entry += getNiceQuote();
							entry += "</i>";
						}
						list.add(Html.fromHtml(entry));
					}			
				
					adapter.notifyDataSetChanged();  //signal the graphics to update
					Log.d("cloud", "Leaderboard updated!"); 
				}
				
			}  else {
				Log.d("cloud", "Leaderboard unable to refresh due to adapter or list not loaded");
			}
		} else {
			Log.d("cloud", "Leaderboard unable to refresh due to activity not loaded");
		}
	}
}
