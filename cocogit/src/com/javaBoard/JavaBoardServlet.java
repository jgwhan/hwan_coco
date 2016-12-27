package com.javaBoard;

import java.io.IOException;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.freeBoard.BoardDTO;
import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/javaBoard/*")
public class JavaBoardServlet extends MyServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		MyUtil util = new MyUtil();
		JavaBoardDAO dao = new JavaBoardDAO();
		
		if(uri.indexOf("list.do") != -1) // list.do면 여기로들어와 
		{
			String page = req.getParameter("page");
			int currentPage = 1;
			if(page != null)
				currentPage = Integer.parseInt(page);
			
			int numPerPage = 5;
			String rows = req.getParameter("rows");
			if(rows != null)
				numPerPage = Integer.parseInt(rows);
			
			// 검색 컬럼, 검색 값 
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");
			if(searchKey == null)
			{
				searchKey = "subject";
				searchValue = "";
			}
			if(req.getMethod().equalsIgnoreCase("GET"))
			{
				searchValue = URLDecoder.decode(searchValue, "UTF-8");
			}
			
			int dataCount, tatalPage;
			/////////////////////////////////
			
			forward(req, resp, "/WEB-INF/views/javaBoard/list.jsp");
		}
				
	}

}
