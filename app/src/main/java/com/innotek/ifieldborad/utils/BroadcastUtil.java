package com.innotek.ifieldborad.utils;

import android.content.Context;
import android.content.Intent;

public class BroadcastUtil {
	
	public static final String NEXT_BROADCAST = "com.example.ifiledborad.next";
	
	public static void sendNextBroadcast(Context context){
		Intent broadcastIntent = new Intent();
		broadcastIntent.setAction(NEXT_BROADCAST);
		context.sendBroadcast(broadcastIntent);
	}
	
}
