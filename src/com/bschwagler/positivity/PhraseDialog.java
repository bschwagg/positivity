package com.bschwagler.positivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;
import android.app.NotificationManager;
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
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		// Use the Builder class for convenient dialog construction
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
		LayoutInflater inflater = getActivity().getLayoutInflater();

		View view = inflater.inflate(R.layout.phrase, null);
		builder.setView(view);
		builder.setCancelable(false);
		builder.setPositiveButton("Got it!", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				Toast.makeText(getActivity(), "Clearing  " + notifID + " notification", Toast.LENGTH_SHORT).show();
				if(notifID >= 0){
					NotificationManager nMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
					nMgr.cancel(notifID);
					nMgr.cancelAll(); //TODO: temporary until I figure out why new Dialog isn't being created every .show()
				}
				}
			}
		);
		
		builder.setNegativeButton("Later      ", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				
			}
		});
		// Create the AlertDialog object and return it
		AlertDialog ad = builder.create();
		TextView textView = (TextView) view.findViewById(R.id.phrase_msg);
		if(textView != null)
			textView.setText(msg);
		return ad;
    }
    
    public void setPhrase(String str) {
    	msg = str;
    }
    
    public void setNotifID(int id)
    {
    	notifID = id;
    }
}