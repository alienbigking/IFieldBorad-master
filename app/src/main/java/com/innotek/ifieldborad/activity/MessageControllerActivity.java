package com.innotek.ifieldborad.activity;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;

import com.innotek.ifieldborad.Constants;
import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.database.Message;
import com.innotek.ifieldborad.database.Publicity;
import com.innotek.ifieldborad.database.Video;
import com.innotek.ifieldborad.fragment.StartupFragment;
import com.innotek.ifieldborad.fragment.TextMessageFragment;
import com.innotek.ifieldborad.fragment.VideoMessageFragment;
import com.innotek.ifieldborad.service.MessageService;
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
    private CountDownTimer mTimer=null;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.container);
        mCurrentIndex = 0;
        mPlayList=MessageDispatcher.getPlayList(this);
		initFragment(createFragmentWithBundle());
        setFilter();
        int time=1000*getSharedPreferences(Constants.PREFS_SERVER_TABLE, Context.MODE_PRIVATE).getInt(Constants.REQUEST_TIME,Constants.REQUEST_TIME_DEFAULT);
        mTimer=new CountDownTimer(time, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
//            long second = millisUntilFinished / 1000;
            }

            @Override
            public void onFinish() {
                mTimer.start();
                Intent netIntent = new Intent(MessageControllerActivity.this, UpdateMessageService.class);
                startService(netIntent);
            }
        };
    }

    private void setFilter(){
        IntentFilter nextMessageFilter = new IntentFilter();
        nextMessageFilter.addAction(BroadcastUtil.NEXT_BROADCAST);
        nextMessageFilter.addAction(BroadcastUtil.UPDATE_BROADCAST);
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
            String content=p.getContent();
            args.putString("content",deleteCRLFOnce(content));
            args.putString("publisher", p.getPublisher());
            args.putString("publishTime", p.getPublishTime());
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
    //去掉空行，已减少错位
    private  String deleteCRLFOnce(String input) {
        if (input!=null&&input.length()>0) {
            return input.replaceAll("((\r\n)|\n)[\\s\t ]*(\\1)+", "$1").replaceAll("^((\r\n)|\n)", "");
        } else {
            return input;
        }
    }
    /**
     * Receive broadcast from TextMessageFragment or VideoMessageFragment when these messages
     * was broadcast finished.
     */
    private BroadcastReceiver nextMessageReceiver = new BroadcastReceiver(){
        @Override
        public void onReceive(Context context, Intent intent){
            if(intent.getAction()!=null&&intent.getAction().equals(BroadcastUtil.UPDATE_BROADCAST)){
                mPlayList = MessageDispatcher.getPlayList(MessageControllerActivity.this);
                if(mCurrentIndex>=mPlayList.size())mCurrentIndex=0;
            }else {
                Log.i("-----playSize is ", mPlayList.size() + "");
                if (mCurrentIndex == mPlayList.size() - 1) {
                    // 循环播放
                    mCurrentIndex = -1;
                }
                mCurrentIndex++;
                if (mCurrentIndex < mPlayList.size()) {
                    replaceFragment();
                } else {
                    mCurrentIndex = 0;
				/*
				if(MessageDispatcher.getPlayList(context).size() > 0){
					mPlayList = MessageDispatcher.getPlayList(context);
					
				}
				replaceFragment();
				*/
                }
            }
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        mTimer.start();
    }

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
    public void onBackPressed() {
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
                stopService( new Intent(MessageControllerActivity.this, MessageService.class));
                stopService( new Intent(MessageControllerActivity.this, UpdateMessageService.class));
                finish();
                Intent i = new Intent(MessageControllerActivity.this, StartupActivity.class);
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
                MessageControllerActivity.this.startActivity(i);
            }
        });
        builder.setNegativeButton("直接退出", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
//					//		//回到Home主页
                Intent intent=new  Intent();
                intent.setAction( Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });
        builder.create().show();
    }
}



