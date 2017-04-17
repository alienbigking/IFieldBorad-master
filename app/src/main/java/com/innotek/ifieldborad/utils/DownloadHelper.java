package com.innotek.ifieldborad.utils;


import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

import com.innotek.ifieldborad.activity.StartupActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * 下载帮助类
 */
public class DownloadHelper {

	private static final String TAG = "File Download";
	private Context context;
	private static DownloadProgress downloadProgress;
	//private String imageURL = "http://124.232.143.28:8088/Info_img/";
	private String imageURL;

	public DownloadHelper(Context context){
		this.context = context;
		SharedPreferences settings = context.getSharedPreferences(
				StartupActivity.PREFS_SERVER, Context.MODE_PRIVATE);
		imageURL = settings.getString("broadcastServer", StartupActivity.DEFAULT_BROADCAST_SERVER) + "/Info_img/";
	}

	public interface DownloadProgress{
		void onDownloading(long total, long writed);
	}

	public static void setDownloadProgress(DownloadProgress dp){
		downloadProgress = dp;
	}

	/**
	 *
	 * @param infoId
	 * @param fileName
	 * @return
	 * @throws Exception
	 */
	public boolean downloadFile(int infoId, String fileName) throws Exception{
		Log.i(TAG, "The image url is " + imageURL);
		URL url = new URL(imageURL + infoId + "/" + fileName);
		HttpURLConnection connection = (HttpURLConnection)url.openConnection();
		try{
			InputStream in = connection.getInputStream();
			FileOutputStream out;
			if(connection.getResponseCode() != HttpURLConnection.HTTP_OK){
				return false;
			}
			int bytesRead = 0;
			long hasWrote = 0;
			byte[] buffer = new byte[1024];
			long size = connection.getContentLength();
			if(!fileExists(fileName)){
				out = context.openFileOutput(fileName, Context.MODE_PRIVATE);
				while((bytesRead = in.read(buffer)) > 0){
					downloadProgress.onDownloading(size, hasWrote);
					hasWrote += bytesRead;
					out.write(buffer, 0, bytesRead);
				}
				out.close();
				in.close();
				connection.disconnect();
				Log.i(TAG, fileName + " download complete");
			}else{
				if(connection.getContentLength() > context.openFileInput(fileName).available()){
					Log.i(TAG, fileName + " download again");
					RandomAccessFile raf = new RandomAccessFile(fileName, "rw");
					raf.seek(context.openFileInput(fileName).available());
					while((bytesRead = in.read(buffer)) > 0){
						downloadProgress.onDownloading(size, hasWrote);
						hasWrote += bytesRead;
						raf.write(buffer, 0, bytesRead);
					}
					raf.close();
					connection.disconnect();
					Log.i(TAG, fileName + " download complete");
				}else{
					Log.i(TAG, fileName + " has already downloaded");
				}
			}
			return true;
		}catch(IOException e){
			e.printStackTrace();
			return false;
		}finally{
			connection.disconnect();
		}
	}

	/**
	 *
	 * @param fileName
	 * @return
     */
	private boolean fileExists(String fileName){
		String fileList[] = context.fileList();
		for(int i = 0; i < fileList.length; i++){
			if(fileList[i].equals(fileName)){
				return true;
			}
		}
		return false;
	}
	
/*	private boolean saveFile(String fileName, long fileSize, InputStream inputStream, int storageCode) throws IOException{
		
		long hasWrote = 0;
		FileOutputStream outputStream;
		FileInputStream input;
		
		try{
			if(storageCode == 0){
				Log.i(TAG,  "InternalStorage-----");
				if(fileExists(fileName, storageCode)){
					input = context.openFileInput(fileName);
					hasWrote = input.available();				
				}
				outputStream = context.openFileOutput(fileName, Context.MODE_PRIVATE);
			
			}else{
				Log.i(TAG, "ExternalStorage------");
			
				File f = new File(Environment.getExternalStorageDirectory(), fileName);
				input = new FileInputStream(f);
				hasWrote = input.available();			
				outputStream = new FileOutputStream(f);
			}
		
			return writeBytes(fileSize, hasWrote, inputStream, outputStream);
			
		}catch(Exception e){
			Log.e(TAG, e.getMessage());
			return false;
		}finally{
			inputStream.close();
		}
	}
	
	
	private boolean writeBytes(long fileSize, long hasWrote, InputStream inputStream,
									FileOutputStream outputStream)	{
				
		try{
			if(hasWrote < fileSize){
				Log.i(TAG, "Downloading file size " + fileSize);
				int bytesRead = 0;
				byte[] buffer = new byte[1024];
			
				while((bytesRead = inputStream.read(buffer)) > 0){
					downloadProgress.onDownloading(fileSize, hasWrote);	
					hasWrote += bytesRead;
					outputStream.write(buffer, 0, bytesRead);
				}

			}
			outputStream.close();
			inputStream.close();
			return true;
		}catch(IOException e){
			Log.e(TAG, e.getMessage());
			return false;
		}
	}
	
	*//**
	 *
	 * 		  0表示存储在InternalStorage, 1表示存储在ExternalStorage
	 * @return
	 *//*
	private boolean fileExists(String fileName, int storageCode){
		
		if(storageCode == 0){
			Log.i(TAG, "Find file in internal storage.");
			for(int i = 0; i < context.fileList().length; i++){
				
				if(context.fileList()[i].equals(fileName)){
					Log.i(TAG, fileName + " is already downloaded.");
					return true;
				}
			}
		}else{
			Log.i(TAG, "Find file in external storage.");
			File file = new File(Environment.getExternalStorageDirectory(), fileName);
			if(file.exists()){
				Log.i(TAG, fileName + " is already downloaded.");
				return true;
			}
		}
		Log.i(TAG, fileName + " has not downloaded");
		return false;
	}*/


	public int downloadMessageResources(ArrayList<DownloadObject> utils){
		int counter = 0;
		Iterator<DownloadObject> it = utils.iterator();
		while(it.hasNext()){
			DownloadObject downloadObj = (DownloadObject)it.next();
			try {
				if(downloadFile(downloadObj.getInfoId(), downloadObj.getFilename()))
				{
					counter++;
				}

			}catch(Exception e) {
				e.printStackTrace();
				continue;
			}
		}
		return counter;

	}

}
