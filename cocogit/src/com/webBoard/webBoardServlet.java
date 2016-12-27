package com.webBoard;

import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.qaBoard.qaBoardDAO;
import com.qaBoard.qaBoardDTO;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/webBoard/*")
public class webBoardServlet extends MyServlet{

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		String cp = req.getContextPath();
		MyUtil util = new MyUtil();
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		webBoardDAO dao = new webBoardDAO();
		
		if(uri.indexOf("list.do")!=-1) {
			String page = req.getParameter("page");
			int current_page = 1;
			if(page!=null)
				current_page=Integer.parseInt(page);
			
			String searchKey = req.getParameter("searchKey");
			String searchValue = req.getParameter("searchValue");
			if(searchKey==null) {
				searchKey="subject";
				searchValue="";
			}
			if(req.getMethod().equalsIgnoreCase("GET")) {
				searchValue=URLDecoder.decode(searchValue, "UTF-8");
			}
			
			int numPerPage=10;
			String rows=req.getParameter("rows");
			if(rows!=null)
				numPerPage=Integer.parseInt(rows);
			
			int dataCount, total_page;
			
			if(searchValue.length()==0)
				dataCount = dao.dataCount();
			else
				dataCount = dao.dataCount(searchKey, searchValue);
			
			total_page=util.pageCount(numPerPage, dataCount);
			if(current_page>total_page)
				current_page = total_page;
			
			int start=(current_page-1)*numPerPage+1;
			int end=current_page*numPerPage;
			List<webBoardDTO> list;
			
			if(searchValue.length()==0)
				list=dao.listBoard(start, end);
			else
				list=dao.listBoard(start, end, searchKey, searchValue);
			
			int listNum,n=0;
			Iterator<webBoardDTO> it = list.iterator();
			while(it.hasNext()) {
				webBoardDTO dto = it.next();
				listNum=dataCount-(start+n-1);
				dto.setListNum(listNum);
				n++;
			}
			
			//String params="";
			String listUrl=cp+"/webBoard/list.do?rows="+numPerPage;
			String articleUrl=cp+"/webBoard/article.do?page="+current_page+"&rows="+numPerPage;
			
			
			if(searchValue.length()!=0) {
				listUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
				articleUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
			}
			String paging=util.paging(current_page, total_page,listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("rows", numPerPage);
			req.setAttribute("searchKey", searchKey);
			req.setAttribute("searchValue", searchValue);
			
			forward(req, resp, "/WEB-INF/views/webBoard/list.jsp"); 
		}else if(uri.indexOf("created.do")!=-1) {
			// 로그인이 안된경우
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			req.setAttribute("mode", "created");
			forward(req, resp, "/WEB-INF/views/webBoard/created.jsp");
		}else if(uri.indexOf("created_ok.do")!=-1) { // 아이디, 글제목, 글내용 
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			webBoardDTO dto = new webBoardDTO();
			
			dto.setUserId(info.getUserId()); // 로그인 정보는 인포가 갖는다!!
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			//dto.setBoardNum(dao.maxBoardNum());
			
			dao.insertBoard(dto);
			
			resp.sendRedirect(cp+"/webBoard/list.do");
		}
	} // process
	
}
