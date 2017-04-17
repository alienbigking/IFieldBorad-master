package com.innotek.ifieldborad.speech;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class SpeechApkInstaller {
	
	public static boolean installFromAssets(Context context, String apk){
		try{
			AssetManager assets = context.getAssets();
			InputStream stream = assets.open(apk);
			
			if(stream == null){
				Toast.makeText(context, "assets has no apk", Toast.LENGTH_LONG).show();
				return false;
			}
			
			String path = Environment.getExternalStorageDirectory().getPath();
			File dir = new File(path);
			if(!dir.exists()){
				dir.mkdir();
			}
			
			String apkPath = path + "/SpeechService.apk";
			File file = new File(apkPath);
			
			if(!writeStreamToFile(stream, file)){
				return false;
			}
			
			installApk(context, apkPath);
		}catch(Exception e){
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	private static boolean writeStreamToFile(InputStream stream, File file){
		OutputStream output = null;
		try{
			output = new FileOutputStream(file);
			final byte[] buffer = new byte[1024];
			int read;
			while((read = stream.read(buffer)) != -1){
				output.write(buffer, 0 ,read);
			}
			output.flush();
		}catch(Exception e1){
			e1.printStackTrace();
			return false;
		}finally{
			try{
				output.close();
				stream.close();
			}catch(IOException e){
				e.printStackTrace();
				return false;
			}
		}
		return true;
		
	}
	
	private static void installApk(Context context, String apkPath){
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setDataAndType(Uri.fromFile(new File(apkPath)), "application/vnd.android.package-archive");
		context.startActivity(intent);
	}
	
}
