package com.freePhoto;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.util.MyServlet;

@WebServlet("/freePhoto/*")
public class FreePhotoServlet extends MyServlet{

	
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		
		String uri=req.getRequestURI();
//		String cp=req.getContextPath();
		
		if(uri.indexOf("list.do")!=-1) 
		{
			forward(req, resp, "/WEB-INF/views/freePhoto/list.jsp");
		}
		
	}

}
