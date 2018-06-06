package com.innotek.ifieldborad.service;


import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;

import com.innotek.ifieldborad.activity.MessageControllerActivity;
import com.innotek.ifieldborad.database.DBAdapter;
import com.innotek.ifieldborad.utils.ServiceConnectionUtil;
import com.innotek.ifieldborad.utils.SoapUtil;
import com.innotek.ifieldborad.utils.UIHandler;

public class MessageService extends IntentService {

	private static final String TAG = "MessageService";
	private DBAdapter adapter;
	private String mMessage;

	public MessageService(){
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		adapter = new DBAdapter(this);
		UIHandler handler = new UIHandler(this, new Handler(getMainLooper()));

		try{
			if(ServiceConnectionUtil.isOnline(this)){
				Log.i(TAG, "update messages");
				mMessage = "正在获取播报信息";
				handler.mainProcessiong(mMessage);
				adapter.saveMessages(this, SoapUtil.getResultsFromSoap(this));
				startMainActivity();
			}else{
				Log.i(TAG, "Can not connect");
				mMessage = "无法连接服务器，请检查网络";
				handler.mainProcessiong(mMessage);
			}
		}catch(Exception e){
			Log.i(TAG, "Soap has exceptions --- " + e.getMessage());
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("SOAP_EXCEPTION");
			sendBroadcast(broadcastIntent);
		}
	}

	private void startMainActivity(){
		Intent i = new Intent(this, MessageControllerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	//Update UI
}
