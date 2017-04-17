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
import com.innotek.ifieldborad.utils.ServiceConnectionUtil;
import com.innotek.ifieldborad.utils.SoapUtil;


public class UpdateMessageService extends IntentService {

	private static final String TAG = "UpdateMessageService";
		
	public UpdateMessageService(){
		super(TAG);
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {	
		DBAdapter adapter = new DBAdapter(this);
		
		try{
			if(!ServiceConnectionUtil.isOnline(this)){
				
				Toast.makeText(this, "Please turn on your network device",
					Toast.LENGTH_SHORT).show();
			}else{	
				
				adapter.saveMessages(this, SoapUtil
						.getResultsFromSoap(this));				
				}	
			}catch(Exception e){
				Toast.makeText(this, "Can not connect to server", Toast.LENGTH_SHORT).show();
			}
	}
	
	
	public static void start(Context context){
		Long updateFrequent =  1L * 1000;		
		boolean isAutoUpdate = true ;
		
		AlarmManager alarmManager  = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
		Intent updateIntent = new Intent(context, UpdateMessageService.class);
		PendingIntent pi = PendingIntent.getService(context, 0, updateIntent, 0);
		
		if(isAutoUpdate){
			alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME,
					SystemClock.elapsedRealtime() + updateFrequent, 
					updateFrequent, pi);
			Log.i(TAG, "Updating in backgroud...");
		}else{
			alarmManager.cancel(pi);
			pi.cancel();
		}
		
		
	}

}
