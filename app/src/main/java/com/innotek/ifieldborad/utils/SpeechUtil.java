package com.innotek.ifieldborad.utils;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

import com.iflytek.speech.ErrorCode;
import com.iflytek.speech.ISpeechModule;
import com.iflytek.speech.InitListener;
import com.iflytek.speech.SpeechConstant;
import com.iflytek.speech.SpeechSynthesizer;
import com.iflytek.speech.SpeechUtility;
import com.iflytek.speech.SynthesizerListener;
import com.innotek.ifieldborad.speech.SpeechApkInstaller;

public class SpeechUtil {

	private static final String API_KEY = "53469b47";
	private static final String APK_NAME = "SpeechService.mp3";
	private SpeechSynthesizer mTts;
	private String mTextForRead;
	private BroadcastCompleteListener mBroadcastCompleteListener;


	/**
	 * Constructor for SpeechUtil
	 * @param context
	 * @param text
	 */
	public SpeechUtil(Context context, String text){

		//If device has not installed SpeechService.apk
		if(SpeechUtility.getUtility(context).queryAvailableEngines() == null ||
				SpeechUtility.getUtility(context).queryAvailableEngines().length <= 0){
			SpeechApkInstaller.installFromAssets(context, APK_NAME);
		}
		SpeechUtility.getUtility(context).setAppid(API_KEY);
		mTextForRead = text;
		mTts = new SpeechSynthesizer(context, mTtsInitListener);
		mTts.setParameter(SpeechConstant.ENGINE_TYPE, "local");
		mTts.setParameter(SpeechSynthesizer.VOICE_NAME, "xiaoyan");
		mTts.setParameter(SpeechSynthesizer.SPEED, "50");
		mTts.setParameter(SpeechSynthesizer.PITCH, "60");
		mTts.setParameter(SpeechSynthesizer.VOLUME, "80");
	}

	//Listen for speech complete, status == 200 
	public interface BroadcastCompleteListener{
		void onBroadcastComplete(int status);
		void onSpeakProgress(int progress);
	}

	public void setBroadcastComplete(BroadcastCompleteListener bc){
		mBroadcastCompleteListener = bc;
	}

	public int startTts(){
		return mTts.startSpeaking(mTextForRead, mTtsListener);
	}

	public void stopTts(){
		mTts.stopSpeaking(mTtsListener);
		mTts.destory();
	}

	/**
	 * Initializing tts listener
	 */
	private InitListener mTtsInitListener = new InitListener() {

		@Override
		public void onInit(ISpeechModule arg0, int code) {
			if (code == ErrorCode.SUCCESS) {
				startTts();
			}
		}
	};


	private SynthesizerListener mTtsListener = new SynthesizerListener.Stub() {
		@Override
		public void onBufferProgress(int progress) throws RemoteException {
		}

		//Speech complete
		@Override
		public void onCompleted(int code) throws RemoteException {
			if(mBroadcastCompleteListener!=null)mBroadcastCompleteListener.onBroadcastComplete(200);
		}

		@Override
		public void onSpeakBegin() throws RemoteException {
		}

		@Override
		public void onSpeakPaused() throws RemoteException {
		}

		@Override
		public void onSpeakProgress(int progress) throws RemoteException {
			if(mBroadcastCompleteListener!=null)mBroadcastCompleteListener.onSpeakProgress(progress);
		}

		@Override
		public void onSpeakResumed() throws RemoteException {
		}
	};
}
