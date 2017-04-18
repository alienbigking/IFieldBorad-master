package com.innotek.ifieldborad.activity;

import java.util.ArrayList;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.database.Message;
import com.innotek.ifieldborad.database.Publicity;
import com.innotek.ifieldborad.database.Video;
import com.innotek.ifieldborad.fragment.TextMessageFragment;
import com.innotek.ifieldborad.fragment.VideoMessageFragment;
import com.innotek.ifieldborad.service.UpdateMessageService;
import com.innotek.ifieldborad.utils.BroadcastUtil;
import com.innotek.ifieldborad.utils.MessageDispatcher;

/**
 * 消息控制器
 * 循环播放信息
 */
public class MessageControllerActivity extends BaseActivity {

	private int mCurrentIndex;
	private ArrayList<Message> mPlayList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.container);
		mCurrentIndex = 0;
		mPlayList = MessageDispatcher.getPlayList(this);
		initFragment(createFragmentWithBundle());
		setFilter();
		UpdateMessageService.start(this);
	}

	private void setFilter(){
		IntentFilter nextMessageFilter = new IntentFilter();
		nextMessageFilter.addAction(BroadcastUtil.NEXT_BROADCAST);
		registerReceiver(nextMessageReceiver, nextMessageFilter);
	}

	/**
	 * Initialize fragment with message bundle
	 */
	private Fragment createFragmentWithBundle(){
		Bundle args = new Bundle();
		args.putInt("total", mPlayList.size());
		args.putInt("current", mCurrentIndex + 1);
		Fragment fragment;
		if(mPlayList.get(mCurrentIndex).getInfoType() == 1){
			Publicity p = (Publicity)mPlayList.get(mCurrentIndex);
			args.putString("title", p.getTitle());
			args.putString("content", p.getContent());
			args.putString("publisher", p.getPublisher());
			args.putString("picture", p.getPicture());
			args.putInt("playTimes", p.getPlayTimes());
			args.putInt("picWaitSeconds", p.getPicWaitSeconds());
			Log.i("publicity info ---> ", p.toString() + "----" + p.getPlayTimes());
			
			fragment = TextMessageFragment.newInstance(args);
		}else{
			Video v = (Video)mPlayList.get(mCurrentIndex);
			args.putString("videoName", v.getVideo());
			args.putInt("playTimes", v.getPlayTimes());
			Log.i("Video info ---> ", v.toString() + "----" + v.getPlayTimes());
			fragment = VideoMessageFragment.newInstance(args);
		}
		return fragment;
	}

	/**
	 * Receive broadcast from TextMessageFragment or VideoMessageFragment when these messages
	 * was broadcast finished.
	 */
	private BroadcastReceiver nextMessageReceiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			Log.i("-----playSize is ", mPlayList.size() + "");
			if(mCurrentIndex == mPlayList.size() - 1){
				// 循环播放
				mCurrentIndex = -1;
			}
			mCurrentIndex++;
			if(mCurrentIndex < mPlayList.size()){
				replaceFragment();
			}else{
				mCurrentIndex = 0;
				/*
				if(MessageDispatcher.getPlayList(context).size() > 0){
					mPlayList = MessageDispatcher.getPlayList(context);
					
				}
				replaceFragment();
				*/
			}			
		}
	};

	/**
	 * 替换fragment
	 */
	private void replaceFragment(){
		try {
			mFragmentManager.beginTransaction().remove(mFragment).commit();
			mFragment = null;
			mFragment = createFragmentWithBundle();
			mFragmentManager.beginTransaction().add(R.id.container, mFragment).commit();
		}catch (Exception e){}
	}

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(nextMessageReceiver);
    }

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {//点击返回键，返回到主页
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_MAIN);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);

		}
		return false;
	}

}



