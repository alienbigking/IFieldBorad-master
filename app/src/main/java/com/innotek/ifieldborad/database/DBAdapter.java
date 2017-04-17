package com.innotek.ifieldborad.database;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Vector;

import org.ksoap2.serialization.SoapObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.innotek.ifieldborad.utils.DownloadHelper;
import com.innotek.ifieldborad.utils.DownloadObject;


public class DBAdapter {

	private static final String TAG = "DB_TAG";
	private static final int DATABASE_VERSION = 3;
	private static final String DB_NAME = "ifieldborad";
	private static final String TABLE_NAME = "messages";
	private static final String KEY_ROWID = "info_id";

	private ArrayList<DownloadObject> utils;


	private DatabaseHelper DBHelper;
	private SQLiteDatabase sqliteDB;

	public DBAdapter(Context context){
		DBHelper = new DatabaseHelper(context);
	}


	/**
	 * Insert message
	 * @param message
	 * @return
	 */
	public long insertMessage(ContentValues message){
		return sqliteDB.insert(TABLE_NAME, null, message);
	}


	/**
	 * 提取 soapObject对象中的属性构建ContentValues对象
	 * @param soapObject
	 * @return
	 */
	private ContentValues buildMessage(SoapObject soapObject){

		ContentValues message = new ContentValues();

		if(Integer.parseInt(soapObject.getPropertyAsString("infoType")) == 1){
			message.put("pic_wait_seconds", Integer.parseInt(soapObject.getPropertyAsString("picWaitSeconds")));
			message.put("read_text", Integer.parseInt(soapObject.getPropertyAsString("readtext")));
			message.put("picture", soapObject.getPropertyAsString("picture"));
			message.put("content", soapObject.getPropertyAsString("content"));

		}else{
			message.put("video", soapObject.getPropertyAsString("video"));
		}

		message.put("title", soapObject.getPropertyAsString("title"));
		message.put("info_type", Integer.parseInt(soapObject.getPropertyAsString("infoType")));
		message.put("publisher", soapObject.getPropertyAsString("publisher"));
		message.put("publisher_userid", Integer.parseInt(soapObject.getPropertyAsString("publisherUserid")));
		message.put("publisher_orgid", Integer.parseInt(soapObject.getPropertyAsString("publisherOrgid")));
		message.put("play_times", Integer.parseInt(soapObject.getPropertyAsString("playTimes")));
		message.put("publish_time", soapObject.getPropertyAsString("publishTime"));
		message.put("start_time", soapObject.getPropertyAsString("startTime"));
		message.put("over_time", soapObject.getPropertyAsString("overTime"));
		message.put("modified_time", soapObject.getPropertyAsString("modifiedTime"));
		message.put("is_read", 0);
		message.put("info_id", Integer.parseInt(soapObject.getPropertyAsString("infoId")));

		return message;
	}


	/**
	 * Save soapObjects
	 * @param context
	 * @param soapObjects
	 */
	public void saveMessages(Context context, Vector<SoapObject> soapObjects){
		DownloadHelper dh = new DownloadHelper(context);
		open();
		deleteMessages();
		for(int i = 0; i < soapObjects.size(); i++){
			SoapObject soapObject = soapObjects.get(i);
			insertMessage(buildMessage(soapObject));
			buildDownloadUtil(soapObject);
			int counter = dh.downloadMessageResources(utils);
			if(counter == utils.size()){
				updateMessageReadyFlag(Integer.parseInt(soapObject.getPropertyAsString("infoId")), 1);
				Log.i("------", Integer.parseInt(soapObject.getPropertyAsString("infoId")) + "");
			}
		}
		close();
	}

	/**
	 * 提取 SoapObject中的属性构建 DownloadObject对象
	 * @param soapObject
	 */
	public void buildDownloadUtil(SoapObject soapObject){
		utils = new ArrayList<DownloadObject>();
		Integer infoId = Integer.parseInt(soapObject.getPropertyAsString("infoId"));

		if(soapObject.getPropertyAsString("infoType").equals("1")){
			String resource = soapObject.getPropertyAsString("picture");
			if(resource != null){
				String[] array = resource.split("#");
				for(int i=0; i< array.length; i++){
					if(!(array[i].equals("")) && !(array[i] == null) && !(array[i].indexOf(".") < 0)){
						utils.add(new DownloadObject(infoId, array[i]));
					}
				}
			}
		}else{
			utils.add(new DownloadObject(infoId, soapObject.getPropertyAsString("video")));

		}
	}



	public boolean deleteMessages(){
		return sqliteDB.delete(TABLE_NAME, null, null) > 0 ;
	}


	public Cursor getAllMessages(Context context){

		return sqliteDB.query(TABLE_NAME,
				new String[]{"info_id", "info_type", "title", "content", "picture",
						"read_text", "pic_wait_seconds", "play_times", "video"},
				"is_ready = " + 1,
				null, null, null, null);
	}


	public Cursor getMessageByRowId(long rowId) throws SQLException{
		Cursor cursor = sqliteDB.query(true, TABLE_NAME, new String[]{"info_id", "info_type", "title", "content", "picture",
				"read_text", "pic_wait_seconds", "play_times", "video"}, KEY_ROWID + "=" +
				rowId, null, null, null, null, null);

		if(cursor != null){
			cursor.moveToFirst();
		}
		return cursor;
	}

	//Update message by infoId
	public boolean updateMessageReadyFlag(int infoId, int isReady){
		ContentValues args = new ContentValues();
		args.put("is_ready", isReady);

		return sqliteDB.update(TABLE_NAME, args, KEY_ROWID + "=" + infoId, null) > 0;
	}

	public ArrayList<DownloadObject> getUtils(){
		return utils;
	}

	/**
	 * Create Database!
	 * Copy database file from assets folder to database folder.
	 */
	private static void copyDatabaseFile(Context context){
		try{
			String dbPath = "/data/data/" + context.getPackageName() + "/databases";

			File f = new File(dbPath);
			if(!f.exists()){
				f.mkdirs();
				f.createNewFile();
				copyDB(context.getAssets().open("ifieldborad"), new FileOutputStream(dbPath + "/ifieldborad"));
			}
		}catch(FileNotFoundException e){
			e.printStackTrace();
		}catch(IOException e){
			e.printStackTrace();
		}
	}

	private static void copyDB(InputStream inputStream, OutputStream outputStream) throws IOException{
		//Copy 1K bytes at a time
		byte[] buffer = new byte[1024];
		int length = 0;
		while((length = inputStream.read(buffer)) > 0){
			outputStream.write(buffer, 0 , length);
		}
		inputStream.close();
		outputStream.close();
		Log.i(TAG, "DB file copyed!");

	}

	/**
	 *
	 * @author david
	 *
	 */
	private static class DatabaseHelper extends SQLiteOpenHelper{

		DatabaseHelper(Context context){
			super(context, DB_NAME, null, DATABASE_VERSION);
			copyDatabaseFile(context);
		}

		@Override
		public void onCreate(SQLiteDatabase sqliteDatabase){

		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
			Log.w(TAG, "Upgrading database from version " + oldVersion + " to " +
					newVersion + ", which will destroy all old data");

			db.execSQL("DROP TABLE IF EXISTS");
		}
	}

	public DBAdapter open() throws SQLException{
		sqliteDB = DBHelper.getWritableDatabase();
		return this;
	}

	public void close(){
		DBHelper.close();
	}
}
