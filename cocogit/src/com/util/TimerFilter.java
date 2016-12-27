package com.util;

import java.io.IOException;

import javax.servlet.Filter;   /// javax.servlet �� import�ؾ���
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

/*
 * study3/board/list.do ��� Request
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
	
	@Override // doFilter()�� ���� TimerFilter ��ü 
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException  
	{
		// Request ��ü ������ �� ���� �κ�
		long before = System.currentTimeMillis();
		
		chain.doFilter(req, resp); // ���� �������� req, resp �ѱ�. �̰� ������ ������ �ȵȴ� 
		// ����(�ι�°)���� �Ǵ� ������ �������̸� servlet �Ǵ� jsp ���� 
		
		// Response ��ü ������ �� ���� �κ�
		long after = System.currentTimeMillis();
		
		String uri = "";
		if(req instanceof HttpServletRequest)
		{
			HttpServletRequest request = (HttpServletRequest)req;
			uri = request.getRequestURI();
			
			config.getServletContext().log(uri+ " : " + (after-before) + "ms......");
									// �α� ����ش� 	// Servlet�� ���� �ð� ���Ѵ� 
		}
	}

	@Override
	public void destroy() {
		
	}

}
