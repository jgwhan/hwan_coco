package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// ServletContextListener : 
// �� Context�� �ʱ�ȭ�ǰų� ������ �� �߻��ϴ� �̺�Ʈ�� ó���ϴ� ������. 
@WebListener // Web.xml���� <listner> �±� ���� annotation ���ε� �� �� �ִ�.
public class WebAppInit implements ServletContextListener
{
	private String pathname = "/WEB-INF/count.txt"; // ������ ���� ��� �� �̸� 
	
	@Override
	public void contextInitialized(ServletContextEvent evt)
	{
		pathname = evt.getServletContext().getRealPath(pathname); // ���� ó���� ���� ��¥ ��� ���ؾ��� 
		loadFile(); 
	}
	 
	@Override
	public void contextDestroyed(ServletContextEvent evt) { 
		// �����ؽ�Ʈ�� ����� ��(������ ����Ǵ� ����) �����
		saveFile();
	}
	
	private void loadFile() // ������ �ִ� ���� �о�� 
	{
		try {
			long toDay, yesterDay, total;
			
			File f = new File(pathname);
			if(! f.exists())
				return;
			
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s;
			s = br.readLine(); // �� �� �о���δ� (���ٸ� ���������ϱ� ������)
			br.close();
			
			if(s != null)
			{
				String[] ss = s.split(":");
				
				if(ss.length == 3)
				{
					toDay = Integer.parseInt(ss[0].trim());
					yesterDay = Integer.parseInt(ss[1].trim());
					total = Integer.parseInt(ss[2].trim());
					
					CountManager.init(toDay, yesterDay, total);
				}
			}
			
		} catch (Exception e) {
		}
	}
	
	private void saveFile() // ������ ���� ���� 
	{
		try {
			long toDay, yesterDay, total;
			
			toDay = CountManager.getToDayCount();
			yesterDay = CountManager.getYesterDayCount();
			total = CountManager.getTotalCount();
			
			String s = toDay + ":" + yesterDay + ":" + total;
			
			PrintWriter out = new PrintWriter(pathname); // �ش� ��ο� ���� 
			out.println(s);
			out.close();
			
		} catch (Exception e) {
		}
	}
}
