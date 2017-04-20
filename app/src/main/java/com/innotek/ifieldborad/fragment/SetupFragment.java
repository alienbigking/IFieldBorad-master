package com.innotek.ifieldborad.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.activity.StartupActivity;

public class SetupFragment extends Fragment {

	private EditText mBroadcastServer;
	private EditText mUpdateServer;
	private EditText mState;
	private EditText mOrgId;
	private Button mButton;
	private SharedPreferences preferences;

	//private static final String TAG = "SETUP_SERVER";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		preferences = getActivity().getSharedPreferences(
				StartupActivity.PREFS_SERVER, Context.MODE_PRIVATE);

	}

	/**
	 * 设置完成监听
	 */
	public interface OnSetFinishedListener{
		void onFinished();
	}

	private OnSetFinishedListener onSetFinishedListener;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		onSetFinishedListener = (OnSetFinishedListener) activity;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_setup, null);

		mBroadcastServer = (EditText)v.findViewById(R.id.edit_broadcast_server);
//		mBroadcastServer.setText("http://192.168.1.148:9090");
		String broadcastServer=preferences.getString("broadcastServer","");
		if(broadcastServer!=null&&broadcastServer.length()>0){
			mBroadcastServer.setText(broadcastServer);
		}
		mUpdateServer = (EditText)v.findViewById(R.id.edit_update_server);
		mState = (EditText)v.findViewById(R.id.edit_state);
		mOrgId = (EditText)v.findViewById(R.id.edit_orgid);
		mButton = (Button)v.findViewById(R.id.button_setup_ok);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				try{//activity关闭虚拟键盘
					InputMethodManager inputMethodManager = (InputMethodManager)
							getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					if(inputMethodManager.isActive())//键盘是打开的状态
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}catch(Exception e){}
				String bIP = mBroadcastServer.getText().toString().equals("") ?
						StartupActivity.DEFAULT_BROADCAST_SERVER : mBroadcastServer.getText().toString();
				String uIP = mUpdateServer.getText().toString().equals("") ?
						StartupActivity.DEFAULT_UPDATE_SERVER : mUpdateServer.getText().toString() ;
				String state = mState.getText().toString().equals("") ?
						"湖南省" : mState.getText().toString();
				preferences.edit().putString("updateServer", uIP).commit();
				preferences.edit().putString("broadcastServer", bIP).commit();
				preferences.edit().putString("state", state).commit();
				preferences.edit().putInt("orgId", Integer.parseInt(TextUtils.isEmpty(mOrgId.getText().toString()) ? "410000" : mOrgId.getText().toString())).commit();
				onSetFinishedListener.onFinished();
				Log.i("-----", "set finished");
			}
		});
		return v;
	}

}
