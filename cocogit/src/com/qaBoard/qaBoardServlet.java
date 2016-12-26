package com.qaBoard;

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
@WebServlet("/qaBoard/*")
public class qaBoardServlet extends MyServlet{

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
		qaBoardDAO dao = new qaBoardDAO();
		
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
			List<qaBoardDTO> list;
			if(searchValue.length()==0)
				list=dao.listBoard(start, end);
			else
				list=dao.listBoard(start, end, searchKey, searchValue);
			
			int listNum,n=0;
			Iterator<qaBoardDTO> it = list.iterator();
			while(it.hasNext()) {
				qaBoardDTO dto = it.next();
				listNum=dataCount-(start+n-1);
				dto.setListNum(listNum);
				n++;
			}
			
			//String params="";
			String listUrl=cp+"/qaBoard/list.do?rows="+numPerPage;
			String articleUrl=cp+"/qaBoard/article.do?page="+current_page+"&rows="+numPerPage;
			
			
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
			
			forward(req, resp, "/WEB-INF/views/qaBoard/list.jsp"); 
		}else if(uri.indexOf("created.do")!=-1) {
			// �α����� �ȵȰ��
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			req.setAttribute("mode", "created");
			forward(req, resp, "/WEB-INF/views/qaBoard/created.jsp");
		}else if(uri.indexOf("created_ok.do")!=-1) { // ���̵�, ������, �۳��� 
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			qaBoardDTO dto = new qaBoardDTO();
			
			dto.setUserId(info.getUserId()); // �α��� ������ ������ ���´�!!
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			//dto.setBoardNum(dao.maxBoardNum());
			
			dao.insertBoard(dto, "created");
			
			resp.sendRedirect(cp+"/qaBoard/list.do");
		}else if(uri.indexOf("article.do")!=-1) {
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
			qaBoardDTO dto=dao.readBoard(boardNum);
			if(dto==null) { // �Խù��� ������ �ٽ� ����Ʈ��
				resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
				return;
			}
			
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
			
			// ������ ������
			qaBoardDTO preReadDto=dao.preReadBoard(dto.getGroupNum(), dto.getOrderNo(), 
					searchKey, searchValue);
			qaBoardDTO nextReadDto=dao.nextReadBoard(dto.getGroupNum(), dto.getOrderNo(), 
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
			
			forward(req, resp, "/WEB-INF/views/qaBoard/article.jsp");
		}else if(uri.indexOf("reply.do")!=-1) {
			//�۴亯��
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			int boardNum=Integer.parseInt(req.getParameter("boardNum"));
			String page = req.getParameter("page");
			String rows=req.getParameter("rows");
			
			qaBoardDTO dto = dao.readBoard(boardNum);
			if(dto==null) { // db���� �����Ͱ� �������� ������!!
				resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
				return;
			}
			
			String s = "["+dto.getSubject()+"]�� ���� �亯�Դϴ�.";
			
			dto.setContent(s);
			req.setAttribute("dto", dto);
			req.setAttribute("mode", "reply");
			req.setAttribute("page", page);
			req.setAttribute("rows", rows);
			
			forward(req,resp,"/WEB-INF/views/qaBoard/created.jsp");
			
		} else if(uri.indexOf("reply_ok.do")!=-1) {
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			// �ʿ��� �Ķ���͵�!
			// subject, content
			// groupNum(�ƹ���), orderNo(��), depth(��),
			// parent(�ƹ����� boardNum)
			// rows, page
			// userId  <- �α����� ���̵�
			
			qaBoardDTO dto = new qaBoardDTO();
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			dto.setGroupNum(Integer.parseInt(req.getParameter("groupNum")));
			dto.setDepth(Integer.parseInt(req.getParameter("depth")));
			dto.setOrderNo(Integer.parseInt(req.getParameter("orderNo")));
			dto.setParent(Integer.parseInt(req.getParameter("parent")));
			
			dto.setUserId(info.getUserId());
			
			String page = req.getParameter("page");
			String rows=req.getParameter("rows");
			
			dao.insertBoard(dto, "reply");
			resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
		}else if(uri.indexOf("delete.do")!=-1) {
			if(info==null) {
				forward(req, resp, "/WEB-INF/view/member/login.jsp");
				return;
			}
			
			int boardNum=Integer.parseInt(req.getParameter("boardNum"));
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			qaBoardDTO dto = dao.readBoard(boardNum);
			
			if(dto!=null &&(info.getUserId().equals("admin") ||dto.getUserId().equals(info.getUserId()))) {
				dao.deleteBoard(boardNum);
			}
			
			resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
		} else if(uri.indexOf("update_ok.do")!=-1) {
			if(info==null) {
				forward(req, resp, "/WEB-INF/view/member/login.jsp");
				return;
			}
			
			qaBoardDTO dto = new qaBoardDTO();
			dto.setBoardNum(Integer.parseInt(req.getParameter("boardNum")));
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			String page = req.getParameter("page");
			String rows = req.getParameter("rows");
			
			
			dao.updateBoard(dto);
			
			resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
		} else if(uri.indexOf("update.do")!=-1) {
			if(info==null) {
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			
			int boardNum=Integer.parseInt(req.getParameter("boardNum"));
			
			String page = req.getParameter("page");
			String rows=req.getParameter("rows");
			
			qaBoardDTO dto = dao.readBoard(boardNum);
			if(dto==null) { // db���� �����Ͱ� �������� ������!!
				resp.sendRedirect(cp+"/qaBoard/list.do?page="+page+"&rows="+rows);
				return;
			}
			
			
			req.setAttribute("dto", dto);
			req.setAttribute("mode", "update");
			req.setAttribute("page", page);
			req.setAttribute("rows", rows);
			
			forward(req,resp,"/WEB-INF/views/qaBoard/created.jsp");
			
		}
		
		
		
		
		
	}

}
