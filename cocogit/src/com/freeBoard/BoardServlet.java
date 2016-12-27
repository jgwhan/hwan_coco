package com.freeBoard;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/freeBoard/*")
public class BoardServlet extends MyServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MyUtil util = new MyUtil();

		req.setCharacterEncoding("UTF-8");
		String cp = req.getContextPath();
		String uri = req.getRequestURI();

		HttpSession session = req.getSession();

		BoardDAO dao = new BoardDAO();
	
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (uri.indexOf("list.do") != -1) { //리스트.
			
			String page=req.getParameter("page");
			int current_page=1;
			if(page!=null) //page값이 null이 아니면, page를 current page로
				current_page=Integer.parseInt(page);
			
			int numPerPage=10;
			String rows = req.getParameter("rows");
			if(rows!=null)
				numPerPage=Integer.parseInt(rows);
			
			int dataCount, total_page;
//			if(searchValue.length()==0)
				dataCount=dao.dataCount();
//			else
//				dataCount=dao.dataCount(searchKey, searchValue);
			
			total_page=util.pageCount(numPerPage, dataCount);  //총페이지수
			if(current_page>total_page)
				current_page=total_page;
				
			int start=(current_page-1)*numPerPage+1;
			int end=current_page*numPerPage;	
				
			List<BoardDTO> list;
			/*if(searchValue.length()==0)*/
				list=dao.listBoard(start, end);
//			else
//				list=dao.listBoard(start, end, searchKey, searchValue);
			
			int listNum, n=0;
			Iterator<BoardDTO> it=list.iterator();
			while(it.hasNext()) {
				BoardDTO dto=it.next();
				listNum=dataCount-(start+n-1);
				dto.setListNum(listNum);
				n++;
			}
			
			String listUrl=cp+"/freeBoard/list.do?rows="+numPerPage;
			String articleUrl=cp+"/freeBoard/article.do?page="+
			           current_page+"&rows="+numPerPage;
			/*if(searchValue.length()!=0) {                         //검색일때,
				listUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
				articleUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
			}*/
			
			String paging=util.paging(current_page,
					total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("rows", numPerPage);
			/*req.setAttribute("searchKey", searchKey);            //검색
			req.setAttribute("searchValue", searchValue);*/
			
			
			forward(req, resp, "/WEB-INF/views/freeBoard/list.jsp");

		} else if (uri.indexOf("created.do") != -1) {
			
			// 글 작성 폼
//			System.out.println(info.getUserId()); 세션들어오는지 test.
			if (info == null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			req.setAttribute("mode", "created");

			forward(req, resp, "/WEB-INF/views/freeBoard/created.jsp");

		} else if (uri.indexOf("created_ok.do") != -1) {
			//
			if (info == null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			

			BoardDTO dto = new BoardDTO();
			// db속에 데이터 추가.
			// dto.setName(req.getParameter("name"));
			dto.setUserId(info.getUserId()); // id는 session이 가지고 있다.
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dao.insertBoard(dto, "created");

			resp.sendRedirect(cp + "/freeBoard/list.do");

		}
	}

}
