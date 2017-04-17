package com.innotek.ifieldborad.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.SurfaceView;

public class AudioPlayer {
	private static final String TAG = "AudioPlayer";
	
	private MediaPlayer mPlayer;
	//private Context ct;
	
	public void stop(){
		if(mPlayer != null){
			mPlayer.stop();
			mPlayer.release();
		}
	}
	
	public void play(final Context context, SurfaceView surfaceView, Uri uri, int mediaType){
		try{
			stop();
			//ct = context;
		
			mPlayer = MediaPlayer.create(context, uri);
			if(mediaType == 1){
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
				mPlayer.setDisplay(surfaceView.getHolder());
			}else{
				mPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			
			}
			mPlayer.start();
		
			mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			
				@Override
				public void onCompletion(MediaPlayer mp) {
					
					Log.i(TAG, "AudioPlayer is stoped.");
					mp.stop();
					BroadcastUtil.sendNextBroadcast(context);
//					Intent broadcastIntent = new Intent();
//					broadcastIntent.setAction(MessageActivity.NEXT_BROADCAST);
//					ct.sendBroadcast(broadcastIntent);
				}
			});
		}catch(Exception e){
			BroadcastUtil.sendNextBroadcast(context);
		}
			
	}
}
