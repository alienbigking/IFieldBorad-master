package com.innotek.ifieldborad.utils;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

public class UIHandler {
	private String message;
	private Handler handler;
	private Context context;
	//Handler handler = new Handler();
	
	public UIHandler(Context context, Handler handler){
		this.context = context;
		this.handler = handler;
	}
	
	public void mainProcessiong(String message){
		this.message = message;
		Thread thread = new Thread(null, doBackgroundProcessing, "Background");
		thread.start();
	}
	
	private Runnable doBackgroundProcessing = new Runnable(){
		@Override
		public void run(){
			backgroundThreadProcessing();
		}
	};
	
	private void backgroundThreadProcessing(){
		handler.post(doUpdateGUI);
	}
	
	private Runnable doUpdateGUI = new Runnable(){
		@Override
		public void run(){
			Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
		}
	};
}
