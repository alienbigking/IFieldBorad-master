package com.innotek.ifieldborad.utils;


import android.os.Environment;

public class StorageUtil {
	
	/**
	 * Check external storage whether usable
	 * @return
	 */
	public static boolean isExternalStorageWritable(){
		String state = Environment.getExternalStorageState();
		if(Environment.MEDIA_MOUNTED.equals(state)){
			return true;
		}
		return false;
	}
	
	
	// Checks if external storage is available to at least read
	public static boolean isExternalStorageReadable(){
		String state = Environment.getExternalStorageState();
		
		if(state.equals(Environment.MEDIA_MOUNTED) || 
				Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)){
			return true;
		}
		return false;
	}	
	

}
