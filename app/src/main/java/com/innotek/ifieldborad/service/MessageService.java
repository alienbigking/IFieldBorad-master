package com.innotek.ifieldborad.service;


import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.innotek.ifieldborad.activity.MessageControllerActivity;
import com.innotek.ifieldborad.database.DBAdapter;
import com.innotek.ifieldborad.utils.ServiceConnectionUtil;
import com.innotek.ifieldborad.utils.SoapUtil;

public class MessageService extends IntentService {

	private static final String TAG = "MessageService";
	private DBAdapter adapter;
	private String mMessage="";
	private Handler handler=null;
	public MessageService(){
		super(TAG);
	}

	@Override
	public void onCreate() {
		super.onCreate();
		handler=new Handler(getMainLooper());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		adapter = new DBAdapter(this);

		try{
			if(ServiceConnectionUtil.isOnline(this)){
				Log.i(TAG, "update messages");
				mMessage="正在获取播报信息";
				handler.post(runnable);
				adapter.saveMessages(this, SoapUtil.getResultsFromSoap(this));
				handler.removeCallbacks(runnable);
				startMainActivity();
			}else{
				Log.i(TAG, "Can not connect");
				mMessage="无法连接服务器，请检查网络";
				handler.post(runnable);
			}
		}catch(Exception e){
			Log.i(TAG, "Soap has exceptions --- " + e.getMessage());
			Intent broadcastIntent = new Intent();
			broadcastIntent.setAction("SOAP_EXCEPTION");
			sendBroadcast(broadcastIntent);
		}
	}

	@Override
	public void onDestroy() {
		if(handler!=null)handler.removeCallbacks(runnable);
		super.onDestroy();
	}

	private Runnable runnable=new Runnable() {
		@Override
		public void run() {
			Toast.makeText(getApplicationContext(),mMessage,Toast.LENGTH_LONG).show();
			handler.postDelayed(this,5000);
		}
	};
	private void startMainActivity(){
		Intent i = new Intent(this, MessageControllerActivity.class);
		i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(i);
	}
	//Update UI
}
