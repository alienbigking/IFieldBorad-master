package com.innotek.ifieldborad.utils;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.TextView;

/**
 * This widget is for API level lower than 17
 * @author david
 *
 */

public class TextClockForLowLevel extends TextView {

	public TextClockForLowLevel(Context context) {
		super(context);
		
	}
	
	public static CharSequence formatedClock(){
		Date now = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(now);
	//	calendar.setTimeZone(Timezone.get);		
		return DateFormat.format("yyyy-MM-dd hh:mm", calendar);
	}

}
