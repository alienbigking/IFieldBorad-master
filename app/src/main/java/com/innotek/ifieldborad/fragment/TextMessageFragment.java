package com.innotek.ifieldborad.fragment;


import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Paint;
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
	private String[] mFiles;
	private Timer timer = new Timer();
	private int mPlayTimes;
	private boolean isMeasured=false;//是否可以测量TextView高度，用来只执行一次
	//	private SplitScreenEntity mSplitScreenParams=new SplitScreenEntity();
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
		mContent = (TextView)v.findViewById(R.id.text_msg);
		mPublisher = (TextView)v.findViewById(R.id.publisher);
		mClock = (TextView)v.findViewById(R.id.sys_time);
		//TextView 滑动设置
		mContent.setMovementMethod(ScrollingMovementMethod.getInstance());

		String title = getArguments().getString("title");
		String content = getArguments().getString("content");
		mPlayTimes= getArguments().getInt("playTimes");
		// = 1;
		mFiles = getArguments().getString("picture").split("#");
		int current = getArguments().getInt("current");
		mTitle.setText(current +"," + title +"("+ current +"/" +
				getArguments().getInt("total")+ ")");
		mContent.setText(content);
//		mContent.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
//			@Override
//			public boolean onPreDraw() {
//				if(!isMeasured) {//只执行一次
//					isMeasured=true;
//					//判断是否需要分屏播放
//					float height = mContent.getLineCount() * mContent.getLineHeight();
//					int maxHeight=100*mContent.getMeasuredHeight();
//					if (height > maxHeight) {//progress最小是1，内容的1%大于一屏
//						mSplitScreenParams.setTotal((int) height/maxHeight+(height%maxHeight==0?0:1));
//					}
//					beginBroadcast();
//				}
//				return true;
//			}
//		});
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
		String allContent= mContent.getText().toString();
//		if(mSplitScreenParams.getTotal()>1){//多屏
//			if(mSplitScreenParams.getCurrentIndex()==1)mCounter++;//播放第一屏的时候加次数
//			//substring[start,end)
//			if(mSplitScreenParams.getCurrentIndex()==mSplitScreenParams.getTotal()){//最后一屏
//				mSplitScreenParams.setEnd(allContent.length());
//			}else {
//				mSplitScreenParams.setEnd(mSplitScreenParams.getStart() + allContent.length() / mSplitScreenParams.getTotal());
//			}
//			allContent=allContent.substring(mSplitScreenParams.getStart(),mSplitScreenParams.getEnd());
//			mSplitScreenParams.setStart(mSplitScreenParams.getEnd());
//		}else{
		mCounter++;
//		}
		mSpeechUtil = new SpeechUtil(getActivity(),allContent);
		mSpeechUtil.setBroadcastComplete(mSpeechTextProgerss);
		mSpeechUtil.startTts();
//		mSplitScreenParams.setCurrentIndex(mSplitScreenParams.getCurrentIndex()+1);
	}
	private SpeechUtil.BroadcastCompleteListener mSpeechTextProgerss=new SpeechUtil.BroadcastCompleteListener() {
		@Override
		public void onBroadcastComplete(int status) {
			mSpeechUtil.stopTts();
//			if(mCounter < mPlayTimes||mSplitScreenParams.getCurrentIndex()<mSplitScreenParams.getTotal())
			if(mCounter < mPlayTimes)
				beginBroadcast();
			else{
				mCounter = 0;
//				mSplitScreenParams.setCurrentIndex(1);
				BroadcastUtil.sendNextBroadcast(getActivity());
			}
		}

		@Override
		public void onSpeakProgress(int progress) {
			float height=mContent.getLineCount()*mContent.getLineHeight();
			if (height > mContent.getMeasuredHeight()) {//内容高度大于一屏才滚动
//				if(mSplitScreenParams.getTotal()>1){//多段
//					mSplitScreenParams.getCurrentIndex();
//					int y = (int) (height*(100*(mSplitScreenParams.getCurrentIndex()-1)+progress))/(100*mSplitScreenParams.getTotal());
//					//2*mContent.getLineHeight()滚动慢2行，一般来说一屏不至于才2行，比较合适,y必须大于0
//					y = y - 2 * mContent.getLineHeight() > 0 ? y - 2 * mContent.getLineHeight() : y;
//					mContent.setScrollY(y);
//				}else {//一段
				int y = (int) (height * progress / 100);
				//2*mContent.getLineHeight()滚动慢2行，一般来说一屏不至于才2行，比较合适,y必须大于2行
				y = y - 2 * mContent.getLineHeight() ;
				if(y>0)mContent.setScrollY(y);

//				}
			}
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
		BitmapDrawable drawable = (BitmapDrawable)mPicture.getDrawable();
		if(drawable!=null && drawable.getBitmap() != null){
			drawable.getBitmap().recycle();
			mPicture.setImageDrawable(null);
		}
		try{
			File dir = getActivity().getFilesDir();
			mPicture.setImageBitmap(compressImage(dir.getAbsolutePath()+"/"+filename));
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
