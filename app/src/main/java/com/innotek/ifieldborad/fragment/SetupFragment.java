package com.innotek.ifieldborad.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.innotek.ifieldborad.Constants;
import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.activity.MessageControllerActivity;
import com.innotek.ifieldborad.activity.StartupActivity;
import com.innotek.ifieldborad.activity.WebViewActivity;
import com.innotek.ifieldborad.service.UpdateMessageService;

public class SetupFragment extends Fragment {

	private EditText mBroadcastServer;
	private EditText etPlatformName;
	private EditText etManagerTitle;
	private EditText etPrisonName;
	private Button mButton;
	private SharedPreferences preferences;
	private Spinner spinner;
	private int time=Constants.REQUEST_TIME_DEFAULT;

	//private static final String TAG = "SETUP_SERVER";

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		preferences = getActivity().getSharedPreferences(
				Constants.PREFS_SERVER_TABLE, Context.MODE_PRIVATE);

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
//备份
		SharedPreferences temple = getActivity().getSharedPreferences(
				Constants.TEMPLE_TABLE, Context.MODE_PRIVATE);
		mBroadcastServer = (EditText)v.findViewById(R.id.edit_broadcast_server);
		String broadcastServer=temple.getString(Constants.BROADCAST_SERVER,Constants.BROADCAST_SERVER_DEFAULT);
		if(broadcastServer!=null&&broadcastServer.length()>0){
			mBroadcastServer.setText(broadcastServer);
		}
		spinner= (Spinner) v.findViewById(R.id.sp_time);

		initSpinner();
		etPlatformName = (EditText)v.findViewById(R.id.et_platform_name);
		etManagerTitle = (EditText)v.findViewById(R.id.et_manager_title);
		etPrisonName = (EditText)v.findViewById(R.id.et_prison_name);
		etPlatformName.setText(temple.getString(Constants.PLATFORM_NAME,""));
		etManagerTitle.setText(temple.getString(Constants.MANAGER_TITLE,""));
		etPrisonName.setText(temple.getString(Constants.PRISON_NAME,""));

		mButton = (Button)v.findViewById(R.id.button_setup_ok);
		mButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View view) {
				Intent netIntent = new Intent(getActivity(), UpdateMessageService.class);
				try{//activity关闭虚拟键盘
					InputMethodManager inputMethodManager = (InputMethodManager)
							getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					if(inputMethodManager.isActive())//键盘是打开的状态
						inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
				}catch(Exception e){}
				SharedPreferences.Editor edit=preferences.edit();
				String bIP = mBroadcastServer.getText().toString().trim();
				if(bIP.isEmpty()){
					Toast.makeText(getActivity(),R.string.broad_hint,Toast.LENGTH_LONG).show();
				}else{
					String PlatformName = etPlatformName.getText().toString().trim();
					String ManagerTitle = etManagerTitle.getText().toString().trim();
					String PrisonName = etPrisonName.getText().toString().trim();
					if(bIP.length()>0)edit.putString(Constants.BROADCAST_SERVER ,bIP);
					if(PlatformName.length()>0)edit.putString(Constants.PLATFORM_NAME ,PlatformName);
					if(ManagerTitle.length()>0)edit.putString(Constants.MANAGER_TITLE ,ManagerTitle);
					if(PrisonName.length()>0)edit.putString(Constants.PRISON_NAME ,PrisonName);
					edit.putInt(Constants.REQUEST_TIME ,time);
					edit.apply();
					//备份
					SharedPreferences temple = getActivity().getSharedPreferences(
							Constants.TEMPLE_TABLE, Context.MODE_PRIVATE);
					SharedPreferences.Editor templeEdit=temple.edit();
					if(bIP.length()>0)templeEdit.putString(Constants.BROADCAST_SERVER ,bIP);
					if(PlatformName.length()>0)templeEdit.putString(Constants.PLATFORM_NAME ,PlatformName);
					if(ManagerTitle.length()>0)templeEdit.putString(Constants.MANAGER_TITLE ,ManagerTitle);
					if(PrisonName.length()>0)templeEdit.putString(Constants.PRISON_NAME ,PrisonName);
					templeEdit.putInt(Constants.REQUEST_TIME ,time);
					templeEdit.apply();

					onSetFinishedListener.onFinished();
					Log.i("-----", "set finished");
				}

			}
		});
		v.findViewById(R.id.button_update).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				startActivity(new Intent(getActivity(), WebViewActivity.class));
			}
		});
		return v;
	}
	private void initSpinner(){

		//协议初始化
		final int[] protocolArray = getResources().getIntArray(R.array.protocol_params);
		ArrayAdapter protocolAdapter = new ArrayAdapter(getActivity(),
				R.layout.spinner_item,  getResources().getStringArray(R.array.protocol));
		spinner.setAdapter( protocolAdapter);
		spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
				time = protocolArray[position];
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
		spinner.setSelection(1);
	}


}
