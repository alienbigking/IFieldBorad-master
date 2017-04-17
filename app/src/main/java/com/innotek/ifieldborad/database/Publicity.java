package com.innotek.ifieldborad.database;

import android.content.Context;
import android.content.Intent;

import com.innotek.ifieldborad.activity.MessageControllerActivity;

public class Publicity extends Message {

	private int picWaitSeconds;
	private String picture;
	private String content;
	private int readtext;
	private String title;
	private Context context;
	
	
	public Publicity(Context context){
		this.context = context;
	}
	
	@Override
	public void startBroadcast() {

	}


	public int getPicWaitSeconds() {
		return picWaitSeconds;
	}


	public void setPicWaitSeconds(int picWaitSeconds) {
		this.picWaitSeconds = picWaitSeconds;
	}


	public String getPicture() {
		return picture;
	}


	public void setPicture(String picture) {
		this.picture = picture;
	}


	public String getContent() {
		return content;
	}


	public void setContent(String content) {
		this.content = content;
	}


	public int getReadtext() {
		return readtext;
	}


	public void setReadtext(int readtext) {
		this.readtext = readtext;
	}


	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public String toString() {
		return "Publicity{" +
				"picWaitSeconds=" + picWaitSeconds +
				", picture='" + picture + '\'' +
				", content='" + content + '\'' +
				", readtext=" + readtext +
				", title='" + title + '\'' +
				", context=" + context +
				'}';
	}
}
