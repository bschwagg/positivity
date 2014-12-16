package com.bschwagler.positivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.app.NotificationManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ContextMenu;

/**
 * @author Brad
 *
 *	This class creates a dialog to display the reminder message.  
 *
 */
public class PhraseDialog extends DialogFragment  {

	String msg;
	private int notifID;
	private int mProgressStatus = 0;
	static private boolean startedCountdown = false;
	static private boolean isShowing = false;

	CountDownTimer mCountDownTimer;
	AlertDialog ad;
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {

		LayoutInflater inflater = getActivity().getLayoutInflater();
		View view = inflater.inflate(R.layout.phrase, null);

		//If we change view while counting down, don't restart the timer..
		if(isShowing || startedCountdown == false )
		{
			final TextView count = (TextView) view.findViewById(R.id.text_count);
			final ProgressBar progBar = (ProgressBar) view.findViewById(R.id.pbHeaderProgress);
			progBar.setVisibility(ProgressBar.INVISIBLE);
			count.setVisibility(TextView.INVISIBLE);


			// Use the Builder class for convenient dialog construction
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

			builder.setView(view);
			builder.setCancelable(false);
			builder.setPositiveButton("Got it!", null );

			builder.setNegativeButton("Later      ", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
					if(mCountDownTimer != null)
						mCountDownTimer.cancel();
					startedCountdown=false;
				}
			});

			// Create the AlertDialog object and return it
			 ad = builder.create();

			ad.setOnShowListener(new DialogInterface.OnShowListener() {

				@Override
				public void onShow(DialogInterface dialog) {
					final Button b = ad.getButton(AlertDialog.BUTTON_POSITIVE);

					b.setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							
							//Are they NOT using the countdown timer?
							if( ! GlobalsAreBad.getInstance().useCountdown ) {
								//just clean up
								dialogFinishOK();
								ad.dismiss();
								isShowing = false;
								return;
							}
							
							//Countdown already going? That means they clicked the "cancel" button
							if(startedCountdown){
								startedCountdown=false;
								mCountDownTimer.cancel();
								ad.dismiss();
								isShowing = false;
								return;
							}
							startedCountdown = true;
							b.setText("Cancel");
							progBar.setVisibility(ProgressBar.VISIBLE);
							count.setVisibility(TextView.VISIBLE);
							mProgressStatus = 20; //start at 20 seconds
							// Start lengthy operation in a background thread
							mCountDownTimer=new CountDownTimer(20000,1000) {

								@Override
								public void onTick(long millisUntilFinished) {
									Log.v("Log_tag", "Tick of Progress"+ mProgressStatus+ millisUntilFinished);
									mProgressStatus--;
									progBar.setProgress(mProgressStatus);
									String progressStr = "" + mProgressStatus;
									count.setText(progressStr);
								}

								@Override
								public void onFinish() {
									//Short shake to know the countdown is done..
									if(GlobalsAreBad.getInstance().vibEnabled) {
										Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
										if(vibrator != null)
											vibrator.vibrate(100); 
									}
									dialogFinishOK();
									ad.dismiss();
									isShowing = false;
								}
							};
							mCountDownTimer.start();

						}

					});
				}
			});

			TextView textView = (TextView) view.findViewById(R.id.phrase_msg);
			if(textView != null)
				textView.setText(msg);
		}
		
		isShowing = true;
		return ad;
	}

	private void dialogFinishOK()
	{
		//	Toast.makeText(getActivity(), "Clearing  " + notifID + " notification", Toast.LENGTH_SHORT).show();
		if(notifID >= 0){
			NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
			nMgr.cancel(notifID);
			nMgr.cancelAll(); //TODO: temporary until I figure out why new Dialog isn't being created every .show()
		}

		// Add a point to our score!
		//TODO: Move this all to the cloud
		SharedPreferences settings = getActivity().getSharedPreferences("UserData", 0);
		int points = settings.getInt("points", 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("points", points+1);
		editor.commit();
		
		if( GlobalsAreBad.getInstance().myParseObject != null){
			Log.d("cloud", "Saving score for " + GlobalsAreBad.getInstance().myParseObject.getString("username") + " to the cloud!");
			GlobalsAreBad.getInstance().myParseObject.put("points", points);
			GlobalsAreBad.getInstance().myParseObject.put("countdown", GlobalsAreBad.getInstance().useCountdown);
			GlobalsAreBad.getInstance().myParseObject.saveInBackground();
		}
		
		Toast.makeText(getActivity(), "Nice! You earned +1 points", Toast.LENGTH_SHORT).show();
	}

	public void setPhrase(String str) {
		msg = str;
	}


	public void setNotifID(int id)
	{
		notifID = id;
	}

	public boolean isShowing() {
		return isShowing;
	}
}