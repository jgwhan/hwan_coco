package com.freeBoard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/freeBoard/*")
public class FreeBoardServlet extends MyServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MyUtil myutil = new MyUtil();
		
		req.setCharacterEncoding("UTF-8");		
		String cp=req.getContextPath();
		String uri=req.getRequestURI();
		
		if(uri.indexOf("list.do")!=-1) 
		{
			forward(req, resp, "/WEB-INF/views/freeBoard/list.jsp");
			
		}
		
	}
}
