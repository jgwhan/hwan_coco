package com.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.PrintWriter;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

// ServletContextListener : 
// 웹 Context가 초기화되거나 종료할 때 발생하는 이벤트를 처리하는 리스너. 
@WebListener // Web.xml에서 <listner> 태그 말고 annotation 으로도 할 수 있다.
public class WebAppInit implements ServletContextListener
{
	private String pathname = "/WEB-INF/count.txt"; // 저장할 파일 경로 및 이름 
	
	@Override
	public void contextInitialized(ServletContextEvent evt)
	{
		pathname = evt.getServletContext().getRealPath(pathname); // 파일 처리할 떄는 진짜 경로 구해야함 
		loadFile(); 
	}
	 
	@Override
	public void contextDestroyed(ServletContextEvent evt) { 
		// 웹컨텍스트가 종료될 때(서버가 종료되는 시점) 실행됨
		saveFile();
	}
	
	private void loadFile() // 서버에 있는 정보 읽어내기 
	{
		try {
			long toDay, yesterDay, total;
			
			File f = new File(pathname);
			if(! f.exists())
				return;
			
			BufferedReader br = new BufferedReader(new FileReader(f));
			String s;
			s = br.readLine(); // 한 줄 읽어들인다 (한줄만 저장했으니까 어차피)
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
	
	private void saveFile() // 서버에 정보 저장 
	{
		try {
			long toDay, yesterDay, total;
			
			toDay = CountManager.getToDayCount();
			yesterDay = CountManager.getYesterDayCount();
			total = CountManager.getTotalCount();
			
			String s = toDay + ":" + yesterDay + ":" + total;
			
			PrintWriter out = new PrintWriter(pathname); // 해당 경로에 저장 
			out.println(s);
			out.close();
			
		} catch (Exception e) {
		}
	}
}
