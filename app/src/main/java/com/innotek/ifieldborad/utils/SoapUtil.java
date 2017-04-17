package com.innotek.ifieldborad.utils;

import java.util.Vector;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import com.innotek.ifieldborad.activity.StartupActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.Toast;

public class SoapUtil {
	
	private static final String TAG = "SoapUtil";
		
	/**
	 * Get messages from server
	 * @return
	 */
	public static Vector<SoapObject> getResultsFromSoap(Context context){
		
		SharedPreferences settings = context.getSharedPreferences(StartupActivity.PREFS_SERVER,
												Context.MODE_PRIVATE);
		
		String serverIP = settings.getString("broadcastServer", StartupActivity.DEFAULT_BROADCAST_SERVER);
		// http://service.InfoManage.com/
		String nameSpace = "http://service.InfoManage.com/";
		// /InfomationManagement/GetInfomationInfoList?wsdl
		String serviceURL = serverIP + "/InfomationManagement/ws/GetInfomationInfoList?wsdl";
		
		int orgId = settings.getInt("orgId", 410000);
		
		Log.i(TAG, "The orgId is: " + orgId);
		
		String method = "getInfomationInfoList";		
		SoapObject request = new SoapObject(nameSpace, method);
		request.addProperty("parentid", orgId);
		
		SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
		envelope.dotNet = false;
		envelope.bodyOut = request;
		
		(new MarshalBase64()).register(envelope);
		
		HttpTransportSE ht = new HttpTransportSE(serviceURL);
		ht.debug = true;
		
		try{
			ht.call(null, envelope);
			if(envelope.getResponse() != null){
				@SuppressWarnings("unchecked")
				Vector<SoapObject> messages = (Vector<SoapObject>)envelope.getResponse();
				Log.i(TAG, "message----" + messages.size());
				return messages;
			}else{
				Log.i(TAG, "No Messages");
				return null;
			}
		}catch(Exception e){
			Log.e(TAG, "Soap Failed", e);			
		}
		return null;
	}
}
