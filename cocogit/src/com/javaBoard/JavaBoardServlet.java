package com.javaBoard;

import java.io.IOException;
import java.io.PrintWriter;
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

@WebServlet("/javaBoard/*")
public class JavaBoardServlet extends MyServlet 
{
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		if (uri.indexOf("list.do") != -1) {
			list(req, resp);
		} else if (uri.indexOf("created.do") != -1) {
			createdForm(req, resp);
		} else if (uri.indexOf("created_ok.do") != -1) {
			createdSubmit(req, resp);
		} else if (uri.indexOf("article.do") != -1) {
			article(req, resp);
		} else if (uri.indexOf("update.do") != -1) {
			updateForm(req, resp);
		} else if (uri.indexOf("update_ok.do") != -1) {
			updateSubmit(req, resp);
		} else if (uri.indexOf("delete.do") != -1) {
			delete(req, resp);
		} else if (uri.indexOf("listReply.do") != -1) {
			listReply(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			deleteReply(req, resp);
		}
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();

		JavaBoardDAO dao = new JavaBoardDAO();
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("page");
		int current_page = 1;
		
		if (page != null)
			current_page = Integer.parseInt(page);

		// �˻�
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}

		if (req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "utf-8");
		}

		int dataCount; // ��ü ������ ����
		if (searchValue.length() == 0)
			dataCount = dao.dataCount();
		else
			dataCount = dao.dataCount(searchKey, searchValue);

		int numPerPage = 10;
		int total_page = util.pageCount(numPerPage, dataCount);

		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * numPerPage + 1;
		int end = current_page * numPerPage;

		// �Խù� ��������
		List<JavaBoardDTO> list = null;
		if (searchValue.length() == 0)
			list = dao.listBoard(start, end);
		else
			list = dao.listBoard(start, end, searchKey, searchValue);

		int listNum, n = 0;
		Iterator<JavaBoardDTO> it = list.iterator();
		
		while (it.hasNext()) 
		{
			JavaBoardDTO dto = it.next();
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			n++;
		}

		String params = "";
		if (searchValue.length() != 0) {
			// �˻��� ��� �˻��� ���ڵ�
			searchValue = URLEncoder.encode(searchValue, "utf-8");
			params = "searchKey=" + searchKey + "&searchValue=" + searchValue;
		}

		// ����¡ ó��
		String listUrl = cp + "/javaBoard/list.do";
		String articleUrl = cp + "/javaBoard/article.do?page=" + current_page;
		if (params.length() != 0) {
			listUrl += "?" + params;
			articleUrl += "&" + params;
		}

		String paging = util.paging(current_page, total_page, listUrl);

		// �������� JSP�� �ѱ� �Ӽ�
		req.setAttribute("list", list);
		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("paging", paging);
		req.setAttribute("articleUrl", articleUrl);

		// JSP�� ������
		forward(req, resp, "/WEB-INF/views/javaBoard/list.jsp");
	}
	
	// �۾��� ��
	private void createdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		req.setAttribute("mode", "created");
		String path = "/WEB-INF/views/javaBoard/created.jsp";
		forward(req, resp, path);
	}
	
	private void createdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �� ����
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		JavaBoardDTO dto = new JavaBoardDTO();

		// userId�� ���ǿ� ����� ����
		dto.setUserId(info.getUserId());

		// �Ķ����
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.insertBoard(dto);

		resp.sendRedirect(cp + "/javaBoard/list.do");
	}

	// �ۺ���
	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// �Ķ���� : num, page, [searchKey, searchValue]
		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}

		searchValue = URLDecoder.decode(searchValue, "utf-8");

		// ��ȸ�� ����
		dao.updateHitCount(num);

		// �Խù� ��������
		JavaBoardDTO dto = dao.readBoard(num);
		if (dto == null) { // �Խù��� ������ �ٽ� ����Ʈ��
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// ������ ������
		JavaBoardDTO preReadDto = dao.preReadBoard(dto.getNum(), searchKey, searchValue);
		JavaBoardDTO nextReadDto = dao.nextReadBoard(dto.getNum(), searchKey, searchValue);

		// ����Ʈ�� ������/�����ۿ��� ����� �Ķ����
		String params = "page=" + page;
		if (searchValue.length() != 0) {
			params += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}

		// JSP�� ������ �Ӽ�
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("params", params);
		req.setAttribute("preReadDto", preReadDto);
		req.setAttribute("nextReadDto", nextReadDto);

		req.setAttribute("searchKey", searchKey);
		req.setAttribute("searchValue", searchValue);

		// ������
		String path = "/WEB-INF/views/javaBoard/article.jsp";
		forward(req, resp, path);
	}
	
	// ���� ��
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");
		int num = Integer.parseInt(req.getParameter("num"));
		JavaBoardDTO dto = dao.readBoard(num);

		if (dto == null) {
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		// �Խù��� �ø� ����ڰ� �ƴϸ�
		if (!dto.getUserId().equals(info.getUserId())) {
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("mode", "update");

		String path = "/WEB-INF/views/javaBoard/created.jsp";
		forward(req, resp, path);
	}

	// ���� �Ϸ�
	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");

		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		JavaBoardDTO dto = new JavaBoardDTO();
		dto.setNum(Integer.parseInt(req.getParameter("num")));
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.updateBoard(dto);

		resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
	}

	// ����
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");
		int num = Integer.parseInt(req.getParameter("num"));
		JavaBoardDTO dto = dao.readBoard(num);

		if (dto == null) {
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		// �Խù��� �ø� ����ڳ� admin�� �ƴϸ�
		if (!dto.getUserId().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
			return;
		}

		// bbsReply ���̺��� ON DELETE CASCADE �ɼ����� bbs ���̺��� �����Ͱ� �������� �ڵ� ������
		dao.deleteBoard(num);
		resp.sendRedirect(cp + "/javaBoard/list.do?page=" + page);
	}

	// ���� ����Ʈ 
	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		JavaBoardDAO dao = new JavaBoardDAO();
		MyUtil util = new MyUtil();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNo = req.getParameter("pageNo");
		int current_page = 1;
		
		if (pageNo != null)
			current_page = Integer.parseInt(pageNo);

		int numPerPage = 5;
		int total_page = 0;
		int dataCount = 0;

		dataCount = dao.dataCountReply(num);
		total_page = util.pageCount(numPerPage, dataCount);
		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * numPerPage + 1;
		int end = current_page * numPerPage;

		// ����Ʈ�� ����� ������
		List<JavaReplyDTO> list = dao.listReply(num, start, end);

		// ���͸� <br>
		Iterator<JavaReplyDTO> it = list.iterator();
		while (it.hasNext()) {
			JavaReplyDTO dto = it.next();
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}

		// ����¡ó��(�μ�2�� ¥�� js�� ó��)
		String paging = util.paging(current_page, total_page);

		req.setAttribute("list", list);
		req.setAttribute("pageNo", current_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("total_page", total_page);
		req.setAttribute("paging", paging);

		// ������
		String path = "/WEB-INF/views/javaBoard/listReply.jsp";
		forward(req, resp, path);
	}

	// ���� ����
	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		String state="true";
		if (info == null) { // �α��ε��� ���� ���
			state="loginFail";
		} else {
			int num = Integer.parseInt(req.getParameter("num"));
			JavaReplyDTO rdto = new JavaReplyDTO();
			rdto.setNum(num);
			rdto.setUserId(info.getUserId());
			rdto.setContent(req.getParameter("content"));

			int result=dao.insertReply(rdto);
			if(result==0)
				state="false";
		}

		StringBuffer sb=new StringBuffer();
		sb.append("{");
		sb.append("\"state\":"+"\""+state+"\"");
		sb.append("}");
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.println(sb.toString());
		
	}

	// ���� ���� 
	private void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		int replyNum = Integer.parseInt(req.getParameter("replyNum"));
		String userId=req.getParameter("userId");
		
		String state="false";
		if (info == null) { // �α��ε��� ���� ���
			state="loginFail";
		} else if(info.getUserId().equals("admin") || info.getUserId().equals(userId)) {
			dao.deleteReply(replyNum);
			state="true";
		}
		
		StringBuffer sb=new StringBuffer();
		sb.append("{");
		sb.append("\"state\":"+"\""+state+"\"");
		sb.append("}");
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.println(sb.toString());
	}
}
