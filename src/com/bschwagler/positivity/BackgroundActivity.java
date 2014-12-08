package com.bschwagler.positivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class BackgroundActivity extends Activity {

	//Stuff for Phrase Dialog
	Vector<String> phrases;
	PhraseDialog phraseDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
		setupPhrases();
	}
	

	private void setupPhrases()
	{
		phrases = new Vector<String>();
		InputStream inputStream = getResources().openRawResource(R.raw.phrases);
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, Charset.forName("UTF-8")));
		String line;
		try {
			while((line = br.readLine()) != null){
				System.out.println(inputStream);
				phrases.add(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
	public void onResume() {
		super.onResume();
		//Problem launching dialog from a broadcast receiver?
		//http://stackoverflow.com/questions/4844031/alertdialog-from-within-broadcastreceiver-can-it-be-done
		if(phraseDialog != null) {
			String phrase = phrases.get((int) (Math.random() * phrases.size())) ;

			phraseDialog.setPhrase(  phrase );
			phraseDialog.show(getFragmentManager(), phrase);
			//Toast.makeText(c,), Toast.LENGTH_LONG).show();
		}
	}
	
	
}
