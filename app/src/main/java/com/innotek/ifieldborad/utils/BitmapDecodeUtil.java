package com.innotek.ifieldborad.utils;

import java.io.FileInputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

public class BitmapDecodeUtil {
	private static final String TAG = "BitmapDecodeUtil";
	
	public static int calculateInSampleSize(BitmapFactory.Options options,
												int reqWidth, int reqHeight){
		
		//Log.i(TAG, "The require width/height is: " + reqWidth+"/"+reqHeight);
		//Raw height and width of image
		//int width = options.outWidth;
	    int height = options.outHeight;
		int inSampleSize = 1;
		
		if(height <= reqHeight){
			inSampleSize = 1;
		}else{
			inSampleSize = height / reqHeight;
		}

		return inSampleSize;
	}
	
	
	//InputStream can not preference more than one time
	public static Bitmap decodeSampleBitmapFromResource(Context context,String file,
											int reqWidth, int reqHeight) throws Exception{
		
		//First decode with inJustDecodeBounds = true to check dimensions
		final BitmapFactory.Options options = new BitmapFactory.Options();
		FileInputStream in = context.openFileInput(file);
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeStream(in, null, options);
		in.close();
		//Calculate inSampleSize
		options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
		in = context.openFileInput(file);
		//Decode bitmap with inSampleSize
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeStream(in, null, options);
		in.close();
		return bitmap;
		
	}

}
