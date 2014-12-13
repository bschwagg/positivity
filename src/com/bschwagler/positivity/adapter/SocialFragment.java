package com.bschwagler.positivity.adapter;

import java.util.ArrayList;

import com.bschwagler.positivity.R;

import android.app.Activity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class SocialFragment extends Fragment {

	View rootView;
	ListView listView;
	ArrayAdapter<String> adapter;
	ArrayList<String> list;
	Activity act;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		rootView = inflater.inflate(R.layout.social, container, false);

		// Get ListView object from xml
		listView = (ListView) rootView.findViewById(R.id.list_leaderbaord);

		
		list = new ArrayList<String>();
		 
		// Define a new Adapter
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Forth - the Array of data

		adapter = new ArrayAdapter<String>(getActivity(),
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

	public void update() {
		// Add a point to our score!
		if(act != null)
		{
			SharedPreferences settings = act.getSharedPreferences("UserData", 0);
			int points = settings.getInt("points", 0);

			if(adapter != null && list != null) {
				//TODO: Fetch data and create list
				list.clear();
				list.add("#1 Frank 100pt");
				list.add("#2 Me " + points + "pt");
				adapter.notifyDataSetChanged();
				//listView.requestFocusFromTouch(); // IMPORTANT!
				TextView me = (TextView) listView.getChildAt(1);
				if(me != null)
					me.setTextColor(Color.YELLOW);
				
			}
		}
	}
}
