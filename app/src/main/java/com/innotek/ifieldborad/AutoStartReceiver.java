package com.innotek.ifieldborad;

import com.innotek.ifieldborad.activity.StartupActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Receive boot completed broadcast and then start StartupActivity
 * @author David
 * 
 */
public class AutoStartReceiver extends BroadcastReceiver {
		
	@Override
	public void onReceive(Context context, Intent intent) {		 
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED))
		{   
			Intent i = new Intent(context, StartupActivity.class);
			i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(i);
		}
	}
}
