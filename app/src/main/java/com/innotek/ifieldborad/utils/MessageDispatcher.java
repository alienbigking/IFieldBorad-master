package com.innotek.ifieldborad.utils;


import java.util.ArrayList;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.innotek.ifieldborad.database.DBAdapter;
import com.innotek.ifieldborad.database.Message;
import com.innotek.ifieldborad.database.Publicity;
import com.innotek.ifieldborad.database.Video;

public class MessageDispatcher {

	private static DBAdapter db;
	private static ArrayList<Message> messages = new ArrayList<Message>();
	private static final String TAG = "MessageDispatcher";

	/**
	 * 从本地数据库提取所有符合播放条件的数据，构建成播放列表
	 * @param context
	 * @return
	 */
	public static ArrayList<Message> getPlayList(Context context){
		db = new DBAdapter(context);
		db.open();
		messages.clear();
		Cursor c = db.getAllMessages(context);

		if(c.moveToFirst()){
			do{
				Log.i(TAG, "Get Messages: "+ c.getInt(1));

				if(c.getInt(1) == 1){
					Publicity p = new Publicity(context);
					p.setInfoId(c.getLong(0));
					p.setTitle(c.getString(2));
					p.setContent(c.getString(3));
					p.setPicture(c.getString(4));
					p.setReadtext(c.getInt(5));
					p.setPicWaitSeconds(c.getInt(6));
					p.setPlayTimes(c.getInt(7));
					p.setInfoType(c.getInt(1));
					messages.add(p);
				}else{
					Video v = new Video(context);
					v.setInfoId(c.getLong(0));
					v.setPlayTimes(c.getInt(7));
					v.setVideo(c.getString(8));
					v.setInfoType(c.getInt(1));
					messages.add(v);

				}
			}while(c.moveToNext());
		}
		db.close();

		return messages;
	}




}
