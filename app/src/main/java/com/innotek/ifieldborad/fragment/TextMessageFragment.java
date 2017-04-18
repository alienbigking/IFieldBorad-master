package com.innotek.ifieldborad.fragment;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;


import com.innotek.ifieldborad.R;
import com.innotek.ifieldborad.activity.StartupActivity;
import com.innotek.ifieldborad.utils.BitmapDecodeUtil;
import com.innotek.ifieldborad.utils.BroadcastUtil;
import com.innotek.ifieldborad.utils.SpeechUtil;
import com.innotek.ifieldborad.utils.TextClockForLowLevel;
import com.innotek.ifieldborad.view.BottomScrollView;

/**
 * 文字信息播报
 * @author David
 *
 */
public class TextMessageFragment extends Fragment {

	private static final int PIC_WAIT_SECONDS = 1000 * 10;
	private static final String DEFAULT_STATE = "湖南省";

	private TextView mSystemTitle;
	private TextView mTitle;
	private TextView mContent;
	private TextView mPublisher;
	private TextView mClock;
	private ImageView mPicture;

	private SpeechUtil mSpeechUtil;
	private int mImageLooper = 0;
	private int mCounter = 0;
	private int mPlayTimes = 0;
	private String[] mFiles;
	private TextView tvMeasureText;
	private Timer timer = new Timer();

	final Runnable runnable = new Runnable(){
		public void run(){
			changeImageView(mFiles);
		}
	};


	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState){

		View v = inflater.inflate(R.layout.fragment_message, null);

		SharedPreferences spf = getActivity().getSharedPreferences(StartupActivity.PREFS_SERVER,
				Context.MODE_PRIVATE);

		mSystemTitle = (TextView)v.findViewById(R.id.sys_title);
		mSystemTitle.setText(spf.getString("state", DEFAULT_STATE) +
				getActivity().getResources().getText(R.string.sys_title));
		mTitle = (TextView)v.findViewById(R.id.text_title);
		mTitle.setGravity(Gravity.CENTER);
		tvMeasureText= (TextView) v.findViewById(R.id.measure_text_msg);
		mContent = (TextView)v.findViewById(R.id.text_msg);
		mPublisher = (TextView)v.findViewById(R.id.publisher);
		mClock = (TextView)v.findViewById(R.id.sys_time);
		//TextView 滑动设置
		mContent.setMovementMethod(ScrollingMovementMethod.getInstance());

		String title = getArguments().getString("title");
		String content = getArguments().getString("content");
		mPlayTimes = getArguments().getInt("playTimes");
		//mPlayTimes = 1;
		mFiles = getArguments().getString("picture").split("#");
		int current = getArguments().getInt("current");
		mTitle.setText(current +"," + title +"("+ current +"/" +
				getArguments().getInt("total")+ ")");
		mContent.setText(content);
		tvMeasureText.setText(content);
		String publisher=getArguments().getString("publisher");
		if(publisher!=null&&publisher.length()>0) {
			mPublisher.setVisibility(View.VISIBLE);
			mPublisher.setText(publisher);
		}else{
			mPublisher.setVisibility(View.GONE);
		}
		mClock.setText(TextClockForLowLevel.formatedClock());
		mPicture = (ImageView)v.findViewById(R.id.img_msg);
		beginBroadcast();
		loopImages();
		return v;
	}

	private boolean isScrollToBottom = false;



	//开始播报文本信息
	private void beginBroadcast(){
		mCounter++;
		mSpeechUtil = new SpeechUtil(getActivity(), mContent.getText().toString());
		mSpeechUtil.setSpeechTextProgerss(mSpeechTextProgerss);
		mSpeechUtil.setBroadcastComplete(new SpeechUtil.BroadcastCompleteListener() {
			@Override
			public void onBroadcastComplete(int status) {
				mSpeechUtil.stopTts();
				if(mCounter < mPlayTimes)
					beginBroadcast();
				else{
					mCounter = 0;
					BroadcastUtil.sendNextBroadcast(getActivity());
				}
			}
		});
		mSpeechUtil.startTts();
	}
	private SpeechUtil.SpeechTextProgerss mSpeechTextProgerss=new SpeechUtil.SpeechTextProgerss() {
		@Override
		public void onSpeakProgress(int progress) {
			int y=tvMeasureText.getMeasuredHeight()*progress/100;
			mContent.setScrollY(y);
		}
	};

	@Override
	public void onResume() {
		super.onResume();
	}

	private Handler handler = new Handler();



	private void changeImageView(String[] files){
		String filename;
		if(mImageLooper < files.length){
			filename = files[mImageLooper];
			mImageLooper++;
		} else{
			mImageLooper = 0;
			filename = files[mImageLooper];

		}
//		BitmapDrawable drawable = (BitmapDrawable)mPicture.getDrawable();
//		if(drawable!=null && drawable.getBitmap() != null){
//			drawable.getBitmap().recycle();
//			mPicture.setImageDrawable(null);
//		}
		try{

			File dir = getActivity().getFilesDir();
			String fileName=dir.getAbsolutePath()+"/"+filename;
			mPicture.setImageBitmap(compressImage(dir.getAbsolutePath()+"/"+filename));
//			Bitmap bitmap = BitmapFactory.decodeFile(fileName);
//			mPicture.setImageBitmap(bitmap);
//			mPicture.setImageResource(R.drawable.ic_launcher);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	/**图片按比例大小压缩方法（根据路径获取图片并压缩）：
	 * @param srcPath
	 * @return
	 */
	public  Bitmap compressImage(String srcPath) {
		Bitmap bitmap=null;
		final int MAX_IMAGE_SIZE=100;//最大图片大小200KB
		final float MAX_IMAGE_WIDTH=720f;//最大图片width
		final float MAX_IMAGE_HEIGHT=1280f;//最大图片height
		String path=null;
		try{
			BitmapFactory.Options newOpts = new BitmapFactory.Options();
			//开始读入图片，此时把options.inJustDecodeBounds 设回true了
			newOpts.inJustDecodeBounds = true;
			bitmap = BitmapFactory.decodeFile(srcPath,newOpts);//此时返回bm为空
			newOpts.inJustDecodeBounds = false;
			int w = newOpts.outWidth;
			int h = newOpts.outHeight;
			float hh = MAX_IMAGE_HEIGHT;
			float ww = MAX_IMAGE_WIDTH;
			//缩放比。由于是固定比例缩放，只用高或者宽其中一个数据进行计算即可
			int be = 1;//be=1表示不缩放
			if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
				be = (int) (newOpts.outWidth / ww);
			} else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
				be = (int) (newOpts.outHeight / hh);
			}
			if (be <= 0)
				be = 1;
			//缩放倍数
			newOpts.inSampleSize = be;//设置缩放比例
			//重新读入图片，注意此时已经把options.inJustDecodeBounds 设回false了
			boolean isFinished=false;
			while(!isFinished) {
				try {
					bitmap = BitmapFactory.decodeFile(srcPath, newOpts);
					isFinished=true;
				} catch (OutOfMemoryError e) {
					isFinished=false;
					newOpts.inSampleSize++;
				}
			}
			if(bitmap.getWidth()>MAX_IMAGE_WIDTH||bitmap.getHeight()>MAX_IMAGE_HEIGHT){
				int width = bitmap.getWidth();
				int height = bitmap.getHeight();
				float newWidth = MAX_IMAGE_WIDTH;
				float newHeight = MAX_IMAGE_HEIGHT;
				float scaleWidth = ((float) newWidth) / width;
				float scaleHeight = ((float) newHeight) / height;
				float scale=Math.min(scaleWidth, scaleHeight);
				Matrix matrix = new Matrix();
				matrix.postScale(scale, scale);
				// if you want to rotate the Bitmap
				try {
					bitmap = Bitmap.createBitmap(bitmap, 0, 0, width,
							height, matrix, true);
				}catch (Exception e){

				}
			}
		}catch (Exception e){
			e.printStackTrace();
		}
		return bitmap;
	}

	private Bitmap resizeImage(Bitmap bitmap){

		return bitmap;

	}
	// Update ImageView's image in new thread
	private void loopImages(){
		timer.schedule(new TimerTask(){
			@Override
			public void run(){
				updateGUI();
			}
		},0 ,PIC_WAIT_SECONDS);
	}

	private void updateGUI(){
		handler.post(runnable);
	}

	/**
	 * A helper method for send arguments from MessageAcitivy
	 * @param bundle
	 * @return
	 */
	public static TextMessageFragment newInstance(Bundle bundle){
		TextMessageFragment fragment = new TextMessageFragment();
		fragment.setArguments(bundle);
		return fragment;
	}

	/**
	 * Dip to px
	 * @param context
	 * @param dp
	 * @return
	 */
	private static int dip2px(Context context, int dp){
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int)(dp*scale + .5f);
	}

	@Override
	public void onDestroy(){
		super.onDestroy();
		mSpeechUtil.stopTts();
		handler.removeCallbacks(runnable);
		timer.cancel();
		timer = null;
		BitmapDrawable drawable = (BitmapDrawable)mPicture.getDrawable();
		if(drawable != null && drawable.getBitmap() != null){
			drawable.getBitmap().recycle();
			mPicture.setImageDrawable(null);
		}
		System.gc();
	}
}
