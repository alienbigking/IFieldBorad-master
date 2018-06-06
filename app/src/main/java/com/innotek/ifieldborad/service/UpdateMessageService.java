package com.innotek.ifieldborad.service;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

import com.innotek.ifieldborad.database.DBAdapter;
import com.innotek.ifieldborad.utils.BroadcastUtil;
import com.innotek.ifieldborad.utils.ServiceConnectionUtil;
import com.innotek.ifieldborad.utils.SoapUtil;


public class UpdateMessageService extends IntentService {

	private static final String TAG = "UpdateMessageService";
	private boolean isUpdating=false;

	public UpdateMessageService(){
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {

		try{
			if(!ServiceConnectionUtil.isOnline(this)){
				Toast.makeText(this, "网络异常，请检查网络设置",
						Toast.LENGTH_SHORT).show();
			}else{
				if(!isUpdating){
					isUpdating=true;
					DBAdapter adapter = new DBAdapter(this);
					adapter.saveMessages(this, SoapUtil
							.getResultsFromSoap(this));
					BroadcastUtil.sendUpdateBroadcast(this);//发送广播，更新消息
					isUpdating=false;
				}
			}
		}catch(Exception e){
			isUpdating=false;
		}
	}


}
