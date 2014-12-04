package com.bschwagler.positivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import android.os.Bundle;
import android.os.SystemClock;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.View;
import android.view.View.OnClickListener;


/**
 * @author Brad
 *
 *	This activity is in charge of creating the settings, leader board and dialog windows, 
 *	scheduling the timer with a (always on) background service, and managing
 *	the phrases to be displayed.  It will eventually synchronize events to 
 *	the cloud so a leader board can be displayed.
 *
 */
public class MainActivity extends Activity implements OnClickListener{
	final static private long ONE_SECOND = 1000;
	final static private long TWENTY_SECONDS = ONE_SECOND * 2;
	PendingIntent pi;
	BroadcastReceiver br;
	AlarmManager am;
	Vector<String> phrases;
	PhraseDialog phraseDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		setup();
		findViewById(R.id.the_button).setOnClickListener(this);

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

	private void setup() {
		br = new BroadcastReceiver() {
			@Override
			public void onReceive(Context c, Intent i) {
				//Problem launching dialog from a broadcast receiver?
				//http://stackoverflow.com/questions/4844031/alertdialog-from-within-broadcastreceiver-can-it-be-done
				String phrase = phrases.get((int) (Math.random() * phrases.size())) ;
				phraseDialog.setPhrase(  phrase );
				phraseDialog.show(getFragmentManager(), phrase);
				//Toast.makeText(c,), Toast.LENGTH_LONG).show();
			}
		};
		registerReceiver(br, new IntentFilter("com.brad.wakeup") );
		pi = PendingIntent.getBroadcast( this, 0, new Intent("com.brad.wakeup"),
				0 );
		am = (AlarmManager)(this.getSystemService( Context.ALARM_SERVICE ));
	}

	@Override
	public void onClick(View v) {
		am.set( AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime() + 
				TWENTY_SECONDS, pi );
	}

	@Override
	protected void onDestroy() {
		am.cancel(pi);
		unregisterReceiver(br);
		super.onDestroy();
	}
}