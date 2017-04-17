package com.innotek.ifieldborad.utils;

import java.util.Date;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class Reminder{
	Timer timer;

	public Reminder(int seconds){
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.HOUR_OF_DAY, 12);
		calendar.set(Calendar.MINUTE, 15);
		calendar.set(Calendar.SECOND, 0);
		
		Date date = calendar.getTime();
		
		timer = new Timer();
		timer.schedule(new RemindTask(), date);
	}

	class RemindTask extends TimerTask{
		public void run(){
			System.out.println("Time''s up!");
			timer.cancel();
		}
	}


	public static void main(String args[]){
		System.out.println("About to schedule task.");
		new Reminder(5);
		System.out.println("Task scheduled.");
	}
}
