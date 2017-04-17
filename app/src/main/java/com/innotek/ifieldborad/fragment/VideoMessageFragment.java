package com.innotek.ifieldborad.fragment;

import java.io.File;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;

import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.utils.AudioPlayer;


public class VideoMessageFragment extends Fragment {
	
	private AudioPlayer mAudioPlayer;
	private SurfaceView mSurfaceView;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		View v = inflater.inflate(R.layout.fragment_video, null);
				
		String media = getArguments().getString("videoName");
		File dir = getActivity().getFilesDir();
		
		final Uri uri = Uri.parse(dir + "/" +media);
				
		int dot = media.indexOf(".");
		final int mediaType = media.substring(dot) == ".mp3" ? 0 : 1;
			
		mAudioPlayer = new AudioPlayer();
		mSurfaceView = (SurfaceView)v.findViewById(R.id.surfaceView1);
		
		mSurfaceView.getHolder().addCallback(new SurfaceHolder.Callback() {
			@Override
			public void surfaceDestroyed(SurfaceHolder holder) {
				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder holder) {
				mAudioPlayer.play(getActivity(), mSurfaceView , uri, mediaType);	
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
				
			}
		});
		return v;
	}
	
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		if(mAudioPlayer != null){
			mAudioPlayer.stop();
		}
	}

	/**
	 * A helper method for send arguments from MessageAcitivy
	 * @param bundle
	 * @return
	 */
	public static VideoMessageFragment newInstance(Bundle bundle){
	
		VideoMessageFragment fragment = new VideoMessageFragment();
		fragment.setArguments(bundle);
		return fragment;
	}
}
