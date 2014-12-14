package com.bschwagler.positivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

public class BackgroundActivity extends Activity {

	//Stuff for Phrase Dialog
	Vector<String> phrases = null;
	PhraseDialog phraseDialog = null;
	boolean enableVib;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
		setupPhrases();

	}

	@Override
	protected void onDestroy()
	{
		super.onDestroy();
	}

	private void setupPhrases()
	{
		if(phrases == null) {
			phrases = new Vector<String>();
			InputStream inputStream = getResources().openRawResource(R.raw.phrases);
			BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
			String line;
			try {
				while((line = br.readLine()) != null){
					//	System.out.println(inputStream);
					phrases.add(line);
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(phraseDialog == null)
			phraseDialog = new PhraseDialog();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.background, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	public void onPause() {
		super.onPause();
		if(phraseDialog != null)
			phraseDialog.dismiss(); //make sure we cleanly close this dialog when the activity loses focus
	}

	@Override
	public void onResume() {
		super.onResume();
		//code moved to onPostResume() due to bug!
		//Issue: https://code.google.com/p/android/issues/detail?id=23761
		//See: http://stackoverflow.com/questions/16265733/failure-delivering-result-onactivityforresult/18345899#18345899
		//Problem launching dialog from a broadcast receiver?
		//http://stackoverflow.com/questions/4844031/alertdialog-from-within-broadcastreceiver-can-it-be-done
		if(phraseDialog != null || !phraseDialog.isShowing()) {

			//Grab an ID handle to the notification
			//this can be used by our dialog to dismiss it from the tool bar
			int id = -1;
			Bundle extras = getIntent().getExtras();
			if(extras != null){
				id = extras.getInt("notificationId");
			}
			phraseDialog.setNotifID(id);
			String phrase = phrases.get((int) (Math.random() * phrases.size())) ;
			phraseDialog.setPhrase(  phrase );
			phraseDialog.show(getFragmentManager(), phrase);

			//Toast.makeText(c,), Toast.LENGTH_LONG).show();
		}
	}



}
