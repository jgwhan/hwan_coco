package com.util;

import java.io.IOException;

import javax.servlet.Filter;   /// javax.servlet 꺼 import해야함
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/*
 * study3/board/list.do 라는 Request
 * 	--TimerFilter (Request)
 * BoardServlet 
 * 	--TimerFilter (Response)
 * client
 */

public class TimerFilter implements Filter{

	private FilterConfig config;
	
	@Override
	public void init(FilterConfig config) throws ServletException {
		this.config = config;
	}
	
	@Override // doFilter()로 들어온 TimerFilter 객체 
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException  
	{
		// Request 객체 조작할 때 쓰는 부분
		long before = System.currentTimeMillis();
		
		chain.doFilter(req, resp); // 다음 필터한테 req, resp 넘김. 이거 없으면 진행이 안된다 
		// 다음(두번째)필터 또는 필터의 마지막이면 servlet 또는 jsp 실행 
		
		// Response 객체 조작할 때 쓰는 부분
		long after = System.currentTimeMillis();
		
		String uri = "";
		if(req instanceof HttpServletRequest)
		{
			HttpServletRequest request = (HttpServletRequest)req;
			uri = request.getRequestURI();
			
			config.getServletContext().log(uri+ " : " + (after-before) + "ms......");
									// 로그 찍어준다 	// Servlet이 일한 시간 구한다 
		}
	}

	@Override
	public void destroy() {
		
	}

}
