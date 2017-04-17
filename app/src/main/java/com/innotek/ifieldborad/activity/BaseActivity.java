package com.innotek.ifieldborad.activity;

import com.innotek.ifieldborad.R;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.view.Window;
import android.view.WindowManager;

public class BaseActivity extends FragmentActivity {
		
	protected FragmentManager mFragmentManager;
	protected Fragment mFragment;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setWindowFullScreen();
	}
	
	/**
	 * 
	 * @param fragment
	 */
	protected void initFragment(Fragment fragment){
		mFragmentManager = getSupportFragmentManager();	
		mFragment = mFragmentManager.findFragmentById(R.id.container);
		mFragment = fragment;
		mFragmentManager.beginTransaction().replace(R.id.container, mFragment).commit();
	}

	
	private void setWindowFullScreen(){
		Window win = getWindow();
		win.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		win.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
	}
}
