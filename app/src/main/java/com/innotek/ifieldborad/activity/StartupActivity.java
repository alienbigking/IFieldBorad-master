package com.innotek.ifieldborad.activity;

import java.util.Calendar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.innotek.ifieldborad.Constants;
import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.fragment.SetupFragment;
import com.innotek.ifieldborad.fragment.StartupFragment;
import com.innotek.ifieldborad.service.MessageService;


public class StartupActivity extends BaseActivity implements SetupFragment.OnSetFinishedListener{
	private Fragment fragment;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);

		cleanStorage();
		prepareFragment();
	}

	/**
	 *  准备fragment
	 */
	private void prepareFragment(){
		SharedPreferences settings = getSharedPreferences(Constants.PREFS_SERVER_TABLE, Context.MODE_PRIVATE);
		String broadcastServer = settings.getString("broadcastServer", null);
		if(broadcastServer == null){
			fragment = new SetupFragment();
		}else{
			fragment = new StartupFragment();
		}
		initFragment(fragment);
	}

	/**
	 * 清理内存
	 */
	private void cleanStorage(){
		Calendar cal = Calendar.getInstance();
		if(cal.get(Calendar.DAY_OF_WEEK) == 4){
			Log.i("Clean", "today is 3");
			if(this.getFilesDir().delete())
				Log.i("Clean", "dir is cleaned");
		}
	}

	@Override
	public void onFinished() {
		Log.i("-----", "set finished");
		fragment = new StartupFragment();
		initFragment(fragment);
	}

	@Override
	public void onBackPressed() {
		if(fragment instanceof StartupFragment){
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("请选择退出方式：");
			builder.setPositiveButton("清除缓存退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					SharedPreferences settings = getSharedPreferences(
							Constants.PREFS_SERVER_TABLE, MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.clear();
					editor.commit();
					dialog.dismiss();
					Intent netIntent = new Intent(StartupActivity.this, MessageService.class);
					stopService(netIntent);
					StartupActivity.super.onBackPressed();
				}
			});
			builder.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
//					//		//回到Home主页
//					Intent intent =new  Intent();
//					intent.setAction( Intent.ACTION_MAIN);
//					intent.addCategory(Intent.CATEGORY_HOME);
//					startActivity(intent);
					StartupActivity.super.onBackPressed();
				}
			});
			builder.create().show();
		}else {
			super.onBackPressed();
		}

	}
}
