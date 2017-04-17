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

	//private static final String TAG = "SETUP_SERVER";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
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
		mUpdateServer = (EditText)v.findViewById(R.id.edit_update_server);
		mState = (EditText)v.findViewById(R.id.edit_state);
		mOrgId = (EditText)v.findViewById(R.id.edit_orgid);
		mButton = (Button)v.findViewById(R.id.button_setup_ok);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				String bIP = mBroadcastServer.getText().toString().equals("") ?
						StartupActivity.DEFAULT_BROADCAST_SERVER : mBroadcastServer.getText().toString();
				String uIP = mUpdateServer.getText().toString().equals("") ?
						StartupActivity.DEFAULT_UPDATE_SERVER : mUpdateServer.getText().toString() ;
				String state = mState.getText().toString().equals("") ?
						"湖南省" : mState.getText().toString();
				SharedPreferences settings = getActivity().getSharedPreferences(
						StartupActivity.PREFS_SERVER, Context.MODE_PRIVATE);
				settings.edit().putString("updateServer", uIP).commit();
				settings.edit().putString("broadcastServer", bIP).commit();
				settings.edit().putString("state", state).commit();
				settings.edit().putInt("orgId", Integer.parseInt(TextUtils.isEmpty(mOrgId.getText().toString()) ? "410000" : mOrgId.getText().toString())).commit();
				onSetFinishedListener.onFinished();
				Log.i("-----", "set finished");
			}
		});
		return v;
	}

}
