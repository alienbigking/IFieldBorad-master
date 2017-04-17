package com.innotek.ifieldborad.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.IPackageDeleteObserver;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.PowerManager;
import android.util.Log;

import com.innotek.ifieldborad.activity.StartupActivity;

/**
 * Upgrade APP silently
 * @author David
 *
 */
public class UpgradeApp {
	
	public static final String PACKAGE_NAME = "com.innotek.ifieldborad";
    public static final String APK_NAME = "IFieldBorad.apk";
    
	private static final String TAG = "UpgradeApp";
	private static String updateServer;
	private static int newVersionCode;
	
	
	public static String getUpgradeVersion(String url) throws Exception{
			
		StringBuilder newVersion = new StringBuilder();
		
		HttpClient client = new DefaultHttpClient();
		HttpParams params = client.getParams();
		
		HttpConnectionParams.setConnectionTimeout(params, 3000);
		HttpConnectionParams.setSoTimeout(params, 5000);
		
		HttpResponse response = client.execute(new HttpGet(url));
		HttpEntity entity = response.getEntity();
		
		if(entity != null){
			BufferedReader reader = new BufferedReader( 
					new InputStreamReader(entity.getContent(), "UTF-8"), 1024);
			String line = null;
			while((line = reader.readLine()) != null){
				newVersion.append(line + "\n");
			}
			reader.close();
			
		}
		return newVersion.toString();
	}
	
	
	public static int currentVersion(Context context) throws Exception{
	    int versionCode = -1;

		versionCode = context.getPackageManager().getPackageInfo(PACKAGE_NAME, 0).versionCode;
		return versionCode;
	}
	
	
	private static boolean parseServerVersion(Context context){
		SharedPreferences settings = context.getSharedPreferences(
				StartupActivity.PREFS_SERVER, Context.MODE_PRIVATE);
		
		 updateServer = settings.getString("updateServer", StartupActivity.DEFAULT_UPDATE_SERVER);
		
		try{
			String newVersion = getUpgradeVersion(updateServer + "/version.json");
			JSONArray jsonArray = new JSONArray(newVersion);
			
			if(jsonArray.length() > 0){
				JSONObject obj = jsonArray.getJSONObject(0);
				newVersionCode = obj.getInt("version_code");
				return true;
			}else{
				return false;
			}
			
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			newVersionCode = -1;
			return false;
		}
	}
	
	public static boolean checkToUpgrade(Context context) throws Exception{
		
		if(parseServerVersion(context)){			
			int currentVersionCode = currentVersion(context);
			if(newVersionCode > currentVersionCode){
				Log.i(TAG, "Has new version");
				
				HttpClient client = new DefaultHttpClient();
				HttpGet get = new HttpGet( updateServer + "/IFieldBorad.apk");
				HttpResponse response;
				
				InputStream is;
				FileOutputStream out;
				try{
					response = client.execute(get);
					HttpEntity entity = response.getEntity();
										
					is = entity.getContent();
					if(fileExists(context, APK_NAME)){
						context.deleteFile(APK_NAME);
					}
					//File file = new File(Environment.getExternalStorageDirectory(), "IFieldBorad.apk");
					
					out = context.openFileOutput(APK_NAME, Context.MODE_PRIVATE);
					int bytesRead = 0;
					byte[] buffer = new byte[1024];
					
					while((bytesRead = is.read(buffer)) > 0){
						out.write(buffer, 0, bytesRead);			
					}
					is.close();
					out.close();
					Log.i(TAG, "New version apk is downloaded");
				
					File file = context.getFileStreamPath(APK_NAME);
				    Runtime.getRuntime().exec("chmod 777 " + file.getCanonicalPath());				    
				    installAPK(context);
				    return true;
				}catch(Exception e){
					Log.e(TAG, e.getMessage());
					return false;
				}	
			}else{
				Log.i(TAG, "Does not need to upgrade");
				return false;
			}
		}else{
			Log.i(TAG, "parseJSONError");
			return false;
		}
	}	
		
	
	/**
	 * Install APK
	 * @param context
	 * @return
	 */
	public static boolean installAPK(Context context){

		if(!fileExists(context, APK_NAME)){
			return false;
		}
		
//		int installFlags = 0;
//		installFlags |= PackageManager.INSTALL_REPLACE_EXISTING;
//		File file = context.getFileStreamPath(APK_NAME);
//		PackageManager pm = context.getPackageManager();
//
//		IPackageInstallObserver observer = new MyPackageInstallObserver(context);
//		pm.installPackage(Uri.fromFile(file), observer, installFlags, PACKAGE_NAME);
		return true;
		
	}
	
	// Uninstall silently
	public static void uninstallAPK(Context context){
//		IPackageDeleteObserver observer = new MyPackageDeleteObserver(context);
//		context.getPackageManager().deletePackage(PACKAGE_NAME, observer, 0);
	}
		
	
	private static class MyPackageInstallObserver extends IPackageInstallObserver.Stub{
		private Context context;
	
		public MyPackageInstallObserver(Context context){
			this.context = context;
		}
		
		@Override
		public void packageInstalled(String packageName, int returnCode){
			Log.i("UPGRADE", "Return code is: " + returnCode);	
			if(returnCode == 1){
			  PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
			  pm.reboot("Upgrade");
			}
		}
	}
	

	private static class MyPackageDeleteObserver extends IPackageDeleteObserver.Stub {  
        private Context context;  
        
        public MyPackageDeleteObserver(Context context){  
            this.context=context;  
        }  
        
        @Override
        public void packageDeleted(String packagename, int returnCode){
        	Log.i(TAG, "Delete ReturnCode: " + returnCode); 
        	if(returnCode == 1){
        		Intent intent = new Intent();
        		intent.setAction(Intent.ACTION_PACKAGE_REMOVED);
        		context.sendBroadcast(intent);
        	}
        }
        
    }
	
	private static boolean fileExists(Context context, String fileName){
		String fileList[] = context.fileList();
		for(int i = 0; i < fileList.length; i++){
			if(fileList[i].equals(fileName)){
				return true;
			}
		}
		return false;
	}
	
}
