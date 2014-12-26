package com.bschwagler.positivity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.Vector;

import com.parse.ParseObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 19, 2014
 *
 * Class BackgroundActivity.java Description: 
 * 
 * This is the activity that's run when a message notification arrives.
 * It manages the message to be shown and UI
 *  
 */
public class BackgroundActivity extends Activity {

	//Stuff for Phrase Dialog
	Vector<String> phrases = null;
	String currPhrase = null;
	boolean enableVib;
	private int mProgressStatus = 0;
	private boolean startedCountdown = false;
	private boolean isShowing = false;
	CountDownTimer mCountDownTimer;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_background);
		final TextView count = (TextView) findViewById(R.id.text_count);
		final ProgressBar progBar = (ProgressBar) findViewById(R.id.pbHeaderProgress);
		TextView dismissText = (TextView) findViewById(R.id.text_instructions);
		ImageView iv = (ImageView) findViewById(R.id.phrase_pic);
		
		setupPhrases();
		
		//first time loading or view not yet dismissed
		if(currPhrase == null || isShowing == false)
			currPhrase = phrases.get((int) (Math.random() * phrases.size())) ;

		
		//Set up the view since count down hasn't started
		if( startedCountdown == false )
		{
			progBar.setVisibility(ProgressBar.INVISIBLE);
			count.setVisibility(TextView.INVISIBLE);
			dismissText.setText( Globals.getInstance().useCountdown ? 
					"(Tap screen to start countdown timer)" : "(Tap screen to dismiss)");
			TextView textView = (TextView) findViewById(R.id.phrase_msg);
			if(textView != null)
				textView.setText(currPhrase);
			isShowing = true;
		}
		
		//Set up clicking on the image..
		iv.setOnClickListener(new View.OnClickListener() {

			//Screen is clicked...
		    public void onClick(View v) {
		    	//Ignore the listener once clicked to start.. (they can always use home/back keys to exit)
		    	if(startedCountdown)
		    		return;
		    	
		    	//Kick off the timer if needed...
		    	if( !Globals.getInstance().useCountdown ){
		    		dialogFinishOK();
		    		return;
		    	}
		    	
		    	
		    	startedCountdown = true;
		    	progBar.setVisibility(ProgressBar.VISIBLE);
				count.setVisibility(TextView.VISIBLE);
				mProgressStatus = 20; //start at 20 seconds
				// Start lengthy operation in a background thread
				mCountDownTimer=new CountDownTimer(20000,100) {

					@Override
					public void onTick(long millisUntilFinished) {
						//Log.v("Log_tag", "Tick of Progress"+ mProgressStatus+ millisUntilFinished);
						float alpha;
						mProgressStatus = Math.round( millisUntilFinished/1000.0f ) ;
						progBar.setProgress(mProgressStatus);
						String progressStr = "" + mProgressStatus;
						if(mProgressStatus <= 1){
							progressStr = "&#9786"; //smiley face
							count.setText(Html.fromHtml(progressStr));
							alpha = 255.0f;
						} else {
							alpha = 255.0f - ((float) mProgressStatus - ((float)millisUntilFinished/1000.0f) + 0.5f) * 255.0f;
							count.setText(progressStr);
						}
						count.setTextColor(Color.argb((int) alpha, 0, 0, 255));
						//Log.d("msg", "Alpha: " + alpha);
					}

					@Override
					public void onFinish() {
						//Short shake to know the countdown is done..
						if(Globals.getInstance().vibEnabled) {
							Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
							if(vibrator != null)
								vibrator.vibrate(100); 
						}
						dialogFinishOK();
					}
				};
				mCountDownTimer.start();

			}
		});

	}
	
	private void dialogFinishOK()
	{
	
		NotificationManager nMgr = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
		//nMgr.cancel(notifID);
		nMgr.cancelAll(); //TODO: temporary until I figure out why new Dialog isn't being created every .show()

		if( isShowing) {
			// Add a point to our score!
			SharedPreferences settings =getSharedPreferences("UserData", 0);
			int points = settings.getInt("points", 0);
			SharedPreferences.Editor editor = settings.edit();
			points++;
			editor.putInt("points", points );
			editor.commit();

			ParseObject pObj = ((MainApplication)getApplication()).myParseObject;

			if( pObj != null){
				Log.d("cloud", "Saving score " + points + " for " + pObj.getString("username") + " to the cloud!");
				pObj.put("points", points);
				pObj.put("countdown", Globals.getInstance().useCountdown);
				pObj.saveInBackground();
			}

			Toast.makeText(this, "Nice! You earned +1 points", Toast.LENGTH_SHORT).show();
		}
		startedCountdown = false;
		isShowing = false;
		currPhrase = null;
		currPhrase = null;
		
		this.finish(); //kill off the activity
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

	}

}
