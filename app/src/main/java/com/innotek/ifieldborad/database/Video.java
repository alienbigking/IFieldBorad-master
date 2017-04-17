package com.innotek.ifieldborad.database;

import android.content.Context;


public class Video extends Message {

	private String video;
	private Context context;
	
	public Video(Context context){
		this.context = context;
	}
	
	@Override
	public void startBroadcast() {

	}

	public String getVideo() {
		return video;
	}

	public void setVideo(String video) {
		this.video = video;
	}
}
