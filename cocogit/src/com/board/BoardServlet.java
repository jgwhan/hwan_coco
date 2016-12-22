package com.board;

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
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/board/*")
public class BoardServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("UTF-8");
		
		String uri=req.getRequestURI();
		String cp=req.getContextPath();
		
		HttpSession session=req.getSession();
		// ���ǿ� ����� �α��� ����
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		BoardDAO dao=new BoardDAO();
		MyUtil util=new MyUtil();
		
		if(uri.indexOf("list.do")!=-1) {///////////////////////////////
			
			String page=req.getParameter("page");
			int current_page=1;
			
			if(page!=null)
				current_page=Integer.parseInt(page);
			
			String searchKey=req.getParameter("searchKey");
			String searchValue=req.getParameter("searchValue");
			if(searchKey==null) {
				searchKey="subject";
				searchValue="";
			}
			if(req.getMethod().equalsIgnoreCase("GET")) {
				searchValue=URLDecoder.decode(
						searchValue, "UTF-8");
			}
			
			int numPerPage=10;
			String rows = req.getParameter("rows");
			if(rows!=null)
				numPerPage=Integer.parseInt(rows);

			int dataCount, total_page;
			if(searchValue.length()==0)
				dataCount=dao.dataCount();
			else
				dataCount=dao.dataCount(searchKey, searchValue);
			
			total_page=util.pageCount(numPerPage, dataCount);
			if(current_page>total_page)
				current_page=total_page;
			
			int start=(current_page-1)*numPerPage+1;
			int end=current_page*numPerPage;
			
			List<BoardDTO> list;
			if(searchValue.length()==0)
				list=dao.listBoard(start, end);
			else
				list=dao.listBoard(start, end, searchKey, searchValue);
			
			int listNum, n=0;
			Iterator<BoardDTO> it=list.iterator();
			while(it.hasNext()) {
				BoardDTO dto=it.next();
				listNum=dataCount-(start+n-1);
				dto.setListNum(listNum);
				n++;
			}
			
			String listUrl=cp+"/board/list.do?rows="+numPerPage;
			String articleUrl=cp+"/board/article.do?page="+
			           current_page+"&rows="+numPerPage;
			if(searchValue.length()!=0) {
				listUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
				articleUrl+="&searchKey="+searchKey
						+"&searchValue="
						+URLEncoder.encode(searchValue, "UTF-8");
			}
			
			String paging=util.paging(current_page,
					total_page, listUrl);
			
			req.setAttribute("list", list);
			req.setAttribute("page", current_page);
			req.setAttribute("dataCount", dataCount);
			req.setAttribute("total_page", total_page);
			req.setAttribute("paging", paging);
			req.setAttribute("articleUrl", articleUrl);
			req.setAttribute("rows", numPerPage);
			req.setAttribute("searchKey", searchKey);
			req.setAttribute("searchValue", searchValue);
			
			
			forward(req, resp,"/WEB-INF/views/board/list.jsp");
			
		}else if(uri.indexOf("created.do")!=-1){
			//�α����� �ȵȰ��
			 if(info==null){
				 forward(req,resp,"/WEB-INF/views/member/login.jsp");
				 return;
			 }
			 
			 req.setAttribute("mode", "created");
			 forward(req,resp,"/WEB-INF/views/board/created.jsp");
			
		}else if(uri.indexOf("created_ok.do")!=-1){
			//
			 if(info==null){
				 forward(req,resp,"/WEB-INF/views/member/login.jsp");
				 return;
			 }
			 
			 BoardDTO dto = new BoardDTO();
			 
			 //db�ӿ� ������ �߰�.
			//dto.setName(req.getParameter("name"));
			 dto.setUserId(info.getUserId()); // id�� session�� ������ �ִ�.
			 dto.setSubject(req.getParameter("subject"));
			 dto.setContent(req.getParameter("content"));
			 
			 dao.insertBoard(dto, "created");
			 
			 resp.sendRedirect(cp+"/board/list.do");
			 
		} else if(uri.indexOf("article.do")!=-1) {
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			// �Խù���ȣ,��������ȣ,rows[,searchKey,searchVlue]
			
			int boardNum=Integer.parseInt(req.getParameter("boardNum"));
			String page=req.getParameter("page");
			String rows=req.getParameter("rows");
			String searchKey=req.getParameter("searchKey");
			String searchValue=req.getParameter("searchValue");
			if(searchKey==null) {
				searchKey="subject";
				searchValue="";
			}
			
			searchValue=URLDecoder.decode(searchValue, "utf-8");
			
			// ��ȸ�� ����
			dao.updateHitCount(boardNum);
			
			// �Խù� ��������
			BoardDTO dto=dao.readBoard(boardNum);
			if(dto==null) { // �Խù��� ������ �ٽ� ����Ʈ��
				resp.sendRedirect(cp+"/board/list.do?page="+page+"&rows="+rows);
				return;
			}
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			// ������ ������
			BoardDTO preReadDto=dao.preReadBoard(dto.getGroupNum(), dto.getOrderNo(), 
					searchKey, searchValue);
			BoardDTO nextReadDto=dao.nextReadBoard(dto.getGroupNum(), dto.getOrderNo(), 
					searchKey, searchValue);
			
			// ����Ʈ�� ������/�����ۿ��� ����� �Ķ����
			String params="page="+page+"&rows="+rows;
			if(searchValue.length()!=0) {
				params+="&searchKey="+searchKey
						+"&searchValue="+URLEncoder.encode(searchValue, "utf-8");
			}
			
			// JSP�� ������ �Ӽ�
			req.setAttribute("dto", dto);
			req.setAttribute("page", page);
			req.setAttribute("rows", rows);			
			req.setAttribute("params", params);
			req.setAttribute("preReadDto", preReadDto);
			req.setAttribute("nextReadDto", nextReadDto);
			
			forward(req, resp, "/WEB-INF/views/board/article.jsp");
		
		}else if(uri.indexOf("reply.do")!=-1){
			if(info==null){
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			BoardDTO dto = dao.readBoard(boardNum);
			if(dto==null){
				resp.sendRedirect(cp+"/board/list.do?page="+page+"&rows="+rows);
				return;
			}
			
			String s = "["+dto.getSubject()+"] �� ���� �亯�Դϴ�.";
			
			dto.setContent(s);
			
			req.setAttribute("dto", dto);
			req.setAttribute("mode", "reply");
			req.setAttribute("page", page);
			req.setAttribute("rows", rows);
			
			forward(req,resp,"/WEB-INF/views/board/created.jsp");
			
		}else if(uri.indexOf("reply_ok.do")!=-1){
			if(info==null){
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			// subject,content,groupnum(�ƹ���),orderNo(��),depth(��),parent(�ƹ����� boardnum)
			// rows,page
			//userId(�α����� ���̵� - �亯�ܳ�)
			
			BoardDTO dto = new BoardDTO();
			dto.setSubject(req.getParameter("subject")); 
			dto.setContent(req.getParameter("content"));
			dto.setGroupNum(Integer.parseInt(req.getParameter("groupNum")));
			dto.setDepth(Integer.parseInt(req.getParameter("depth")));
			dto.setOrderNo(Integer.parseInt(req.getParameter("orderNo")));
			dto.setParent(Integer.parseInt(req.getParameter("parent")));
			
			dto.setUserId(info.getUserId());
			
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			
			dao.insertBoard(dto, "reply");
			
			resp.sendRedirect(cp+"/board/list.do?page="+page+"&rows="+rows);
			
			
		} else if(uri.indexOf("delete.do")!=-1){
			if(info==null){
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			BoardDTO dto = dao.readBoard(boardNum);
			if(dto!=null && (info.getUserId().equals("admin") || dto.getUserId().equals(info.getUserId()))){
				dao.deleteBoard(boardNum);
			}
			
			resp.sendRedirect(cp+"/board/list.do?page="+page+"&rows="+rows);
			
			
			
			
			
		}else if(uri.indexOf("update.do")!=-1){ //////////////////////////���� /////
			//�α����� �ȵȰ��

			 if(info==null){
				 forward(req,resp,"/WEB-INF/views/member/login.jsp");
				 return;
			 }			 
			

			int boardNum = Integer.parseInt(req.getParameter("boardNum"));
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			BoardDTO dto = dao.readBoard(boardNum);
			
				
			 req.setAttribute("dto", dto);
			 req.setAttribute("mode", "update");
			 req.setAttribute("page", page);
			 req.setAttribute("rows", rows);
			 
			 forward(req,resp,"/WEB-INF/views/board/created.jsp");
		/////////////////////////////////////////////////////////////////
		}
		
		else if(uri.indexOf("update_ok.do")!=-1){
			
			
			if(info==null){
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
		
			BoardDTO dto = new BoardDTO();
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setBoardNum(Integer.parseInt(req.getParameter("boardNum")));
		  
			
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");

			dao.updateBoard(dto);

			resp.sendRedirect(cp + "/board/list.do?page=" + page + "&rows=" + rows);
			
			
		}
		
		
	} //process()_end

}
