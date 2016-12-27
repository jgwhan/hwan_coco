package com.util;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;


// HttpSessionListener : 
// ������ �����ǰų� �Ҹ�� �� �߻��ϴ� �̺�Ʈ(HttpSessionEvent)�� ó���ϴ� ������
public class CountManager implements HttpSessionListener
{

	private static int currentCount; // ���� �����ڼ� 
	private static long toDayCount, yesterDayCount, totalCount; // ����, ����, ��ü �����ڼ� 
	
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
		
		Timer timer = new Timer(); // ������ �ð����� �ݺ�����  �۾� ������ ���� ! 
		Calendar cal = Calendar.getInstance();
		// ���� �� 12�÷� �ð� �����ϴ� �� ! 
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
		// cron ��ü�� run()�޼ҵ带
		// cal.getTime() �ð� ���� ,�Ϸ翡 �ѹ���, �� 12�ð� �Ǹ�(�ݺ��ð�) ������Ѷ� 
	}
	
	public static void init(long toDay, long yesterDay, long total)
	{
		toDayCount = toDay;
		yesterDayCount = yesterDay;
		totalCount = total;
	}
	
	@Override
	public void sessionCreated(HttpSessionEvent evt) 
	{ // ������ ������ �� �����
		HttpSession session = evt.getSession();
		
		synchronized (this) { 
			 currentCount ++;
			 toDayCount ++;
			 totalCount ++;
		}
		
		session.getServletContext().log(session.getId() + " ���� ����! ");
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent arg0) 
	{ // ������ �Ҹ�� �� ����� 
		synchronized (this) {
			currentCount --;
			
			if(currentCount < 0) // �߸��ؼ� ���� �Ǹ� 0 ���� 
				currentCount = 0;
		}
	}

}
