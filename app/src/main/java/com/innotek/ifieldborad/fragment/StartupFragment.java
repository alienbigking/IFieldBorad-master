package com.innotek.ifieldborad.fragment;

import java.util.Timer;
import java.util.TimerTask;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.innotek.ifieldborad.Constants;
import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.service.MessageService;
import com.innotek.ifieldborad.utils.DownloadHelper;

public class StartupFragment extends Fragment {

	private TextView mDownloadProgress;
	private String mText;
	private TextView mVersionName;
	private TextView tvTitle;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_startup, null);

		mDownloadProgress = (TextView) v.findViewById(R.id.downloadProgress);
		mDownloadProgress.setText("正在读取发布平台");
		mVersionName = (TextView) v.findViewById(R.id.version_name);
		tvTitle= (TextView) v.findViewById(R.id.splash_title);
		SharedPreferences preferences = getActivity().getSharedPreferences(
				Constants.PREFS_SERVER_TABLE, Context.MODE_PRIVATE);
		String title=preferences.getString(Constants.MANAGER_TITLE,getString(R.string.manager_title_defalut))+
				preferences.getString(Constants.PLATFORM_NAME,getString(R.string.platform_name_defalut));
		tvTitle.setText(title);
		return v;
	}

	@Override
	public void onResume() {
		super.onResume();
		try {
			Context context = getActivity();
			mText = "正在连接服务器...";
			String versionName = "";
			try {
				versionName = getString(R.string.current_version) + context.getPackageManager().
						getPackageInfo(context.getPackageName(), 0).versionName;
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}
			mVersionName.setText(versionName);
			//注册下载进度监听借口，更新TextView文本显示下载进度
			DownloadHelper.setDownloadProgress(new DownloadHelper.DownloadProgress() {

				@Override
				public void onDownloading(long total, long writed) {
					String percent = writed * 100 / total + "%";
					mText = "正在下载资源文件      " + percent;
				}
			});
			IntentFilter filter = new IntentFilter();
			filter.addAction("SOAP_EXCEPTION");
			getActivity().registerReceiver(soapExceptionReceiver, filter);

			//Start update message service
			Intent netIntent = new Intent(getActivity(), MessageService.class);
			context.startService(netIntent);
			changeProgress();
		}catch (Exception e){}
	}

	private BroadcastReceiver soapExceptionReceiver = new BroadcastReceiver(){

		@Override
		public void onReceive(Context context, Intent intent){
			Intent netIntent = new Intent(getActivity(), MessageService.class);
			context.startService(netIntent);
		}
	};

	private Timer timer = new Timer();
	final Handler handler = new Handler();

	private void changeProgress(){
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				updateGUI();
			}
		},0 ,1000);
	}

	/**
	 * 修改文字
	 * @param text
	 */
	private void changeTextView(String text){
		mDownloadProgress.setText(text);
	}

	final Runnable runnable = new Runnable(){
		public void run(){
			changeTextView(mText);
		}
	};

	private void updateGUI(){
		handler.post(runnable);
	}

	@Override
	public void onDestroy(){
		try {
			handler.removeCallbacks(runnable);
			timer.cancel();
			timer = null;
			getActivity().unregisterReceiver(soapExceptionReceiver);
		}catch (Exception e){}
		super.onDestroy();
	}
}
