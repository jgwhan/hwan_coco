package com.util;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


// HttpSessionListener : 
// 세션이 생성되거나 소멸될 때 발생하는 이벤트(HttpSessionEvent)를 처리하는 리스너
public class CountManager implements HttpSessionListener
{

	private static int currentCount; // 현재 접속자수 
	private static long toDayCount, yesterDayCount, totalCount; // 오늘, 어제, 전체 접속자수 
	
	public static int getCurrentCount()
	{
		return currentCount;
	}
	
	public static long getToDayCount()
	{
		return toDayCount;
	}
	
	public static long getYesterDayCount()
	{
		return yesterDayCount;
	}
	
	public static long getTotalCount()
	{
		return totalCount;
	}
	
	public CountManager()
	{
		TimerTask cron = new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				yesterDayCount = toDayCount;
				toDayCount = 0;
			}
		};
		
		Timer timer = new Timer(); // 지정된 시간마다 반복적인  작업 수행할 목적 ! 
		Calendar cal = Calendar.getInstance();
		// 오늘 밤 12시로 시간 셋팅하는 것 ! 
		cal.add(Calendar.DATE, 1);
		cal.set(Calendar.HOUR, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
		timer.schedule(cron, cal.getTime(), 1000*60*60*24);
		/*
		 * task /task to be scheduled.
		 * firstTime/ First time at which task is to be executed.
		 * period/ time in milliseconds between successive task executions.
		 */
		// cron 객체의 run()메소드를
		// cal.getTime() 시간 부터 ,하루에 한번씩, 밤 12시가 되면(반복시간) 실행시켜라 
	}
	
	public static void init(long toDay, long yesterDay, long total)
	{
		toDayCount = toDay;
		yesterDayCount = yesterDay;
		totalCount = total;
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent evt) 
	{ // 세션이 생성될 떄 실행됨
		HttpSession session = evt.getSession();
		
		synchronized (this) { 
			 currentCount ++;
			 toDayCount ++;
			 totalCount ++;
		}
		
		session.getServletContext().log(session.getId() + " 세션 생성! ");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) 
	{ // 세션이 소멸될 떄 실행됨 
		synchronized (this) {
			currentCount --;
			
			if(currentCount < 0) // 잘못해서 음수 되면 0 으로 
				currentCount = 0;
		}
	}

}
