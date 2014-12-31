/*+----------------------------------------------------------------------
 ||
 ||  Class com.bschwagler.positivity 
 ||
 ||        Author:  Brad  Dec 31, 2014
 ||
 ||        Description:  
 ||                  
 ||
  +---------------------------------------------------------------------- */
package com.bschwagler.positivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

/**
 * @author Brad    Date: Dec 31, 2014
 *
 * Class PushReceiver.java Description: 
 *
 *  
 */
public class PushReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
            Bundle extras = intent.getExtras();
            String message = extras != null ? extras.getString("com.parse.Data") : "";
            
            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
            toast.show();
          
    }
}