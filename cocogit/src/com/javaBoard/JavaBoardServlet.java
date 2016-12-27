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
public class JavaBoardServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");

		String uri = req.getRequestURI();

		// uri에 따른 작업 구분
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
		// 게시물 리스트
		String cp = req.getContextPath();

		JavaBoardDAO dao = new JavaBoardDAO();
		MyUtil util = new MyUtil();
		
		String page = req.getParameter("page");
		int current_page = 1;
		if (page != null)
			current_page = Integer.parseInt(page);

		// 검색
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}
		// GET 방식인 경우 디코딩
		if (req.getMethod().equalsIgnoreCase("GET")) {
			searchValue = URLDecoder.decode(searchValue, "utf-8");
		}

		// 전체 데이터 개수
		int dataCount;
		if (searchValue.length() == 0)
			dataCount = dao.dataCount();
		else
			dataCount = dao.dataCount(searchKey, searchValue);

		// 전체 페이지 수
		int numPerPage = 10;
		int total_page = util.pageCount(numPerPage, dataCount);

		if (current_page > total_page)
			current_page = total_page;

		// 게시물 가져올 시작과 끝
		int start = (current_page - 1) * numPerPage + 1;
		int end = current_page * numPerPage;

		// 게시물 가져오기
		List<JavaBoardDTO> list = null;
		if (searchValue.length() == 0)
			list = dao.listBoard(start, end);
		else
			list = dao.listBoard(start, end, searchKey, searchValue);

		// 리스트 글번호 만들기
		int listNum, n = 0;
		Iterator<JavaBoardDTO> it = list.iterator();
		while (it.hasNext()) {
			JavaBoardDTO dto = it.next();
			listNum = dataCount - (start + n - 1);
			dto.setListNum(listNum);
			n++;
		}

		String params = "";
		if (searchValue.length() != 0) {
			// 검색인 경우 검색값 인코딩
			searchValue = URLEncoder.encode(searchValue, "utf-8");
			params = "searchKey=" + searchKey + "&searchValue=" + searchValue;
		}

		// 페이징 처리
		String listUrl = cp + "/bbs/list.do";
		String articleUrl = cp + "/bbs/article.do?page=" + current_page;
		if (params.length() != 0) {
			listUrl += "?" + params;
			articleUrl += "&" + params;
		}

		String paging = util.paging(current_page, total_page, listUrl);

		// 포워딩할 JSP로 넘길 속성
		req.setAttribute("list", list);
		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("paging", paging);
		req.setAttribute("articleUrl", articleUrl);

		// JSP로 포워딩
		forward(req, resp, "/WEB-INF/views/javaBoard/list.jsp");
	}
	
	private void createdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글쓰기 폼
		String cp=req.getContextPath();
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		req.setAttribute("mode", "created");
		String path = "/WEB-INF/views/bbs/created.jsp";
		forward(req, resp, path);
	}
	
	private void createdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글 저장
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		JavaBoardDTO dto = new JavaBoardDTO();

		// userId는 세션에 저장된 정보
		dto.setUserId(info.getUserId());

		// 파라미터
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.insertBoard(dto);

		resp.sendRedirect(cp + "/bbs/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 글보기
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		// 파라미터 : num, page, [searchKey, searchValue]
		int num = Integer.parseInt(req.getParameter("num"));
		String page = req.getParameter("page");
		String searchKey = req.getParameter("searchKey");
		String searchValue = req.getParameter("searchValue");
		if (searchKey == null) {
			searchKey = "subject";
			searchValue = "";
		}

		searchValue = URLDecoder.decode(searchValue, "utf-8");

		// 조회수 증가
		dao.updateHitCount(num);

		// 게시물 가져오기
		JavaBoardDTO dto = dao.readBoard(num);
		if (dto == null) { // 게시물이 없으면 다시 리스트로
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));

		// 이전글 다음글
		JavaBoardDTO preReadDto = dao.preReadBoard(dto.getNum(), searchKey, searchValue);
		JavaBoardDTO nextReadDto = dao.nextReadBoard(dto.getNum(), searchKey, searchValue);

		// 리스트나 이전글/다음글에서 사용할 파라미터
		String params = "page=" + page;
		if (searchValue.length() != 0) {
			params += "&searchKey=" + searchKey + "&searchValue=" + URLEncoder.encode(searchValue, "utf-8");
		}

		// JSP로 전달할 속성
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("params", params);
		req.setAttribute("preReadDto", preReadDto);
		req.setAttribute("nextReadDto", nextReadDto);

		req.setAttribute("searchKey", searchKey);
		req.setAttribute("searchValue", searchValue);

		// 포워딩
		String path = "/WEB-INF/views/bbs/article.jsp";
		forward(req, resp, path);
	}
	
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 폼
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");
		int num = Integer.parseInt(req.getParameter("num"));
		JavaBoardDTO dto = dao.readBoard(num);

		if (dto == null) {
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		// 게시물을 올린 사용자가 아니면
		if (!dto.getUserId().equals(info.getUserId())) {
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("mode", "update");

		String path = "/WEB-INF/views/bbs/created.jsp";
		forward(req, resp, path);
	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 수정 완료
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");

		if (req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		JavaBoardDTO dto = new JavaBoardDTO();
		dto.setNum(Integer.parseInt(req.getParameter("num")));
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));

		dao.updateBoard(dto);

		resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 삭제
		String cp = req.getContextPath();

		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		if (info == null) { // 로그인되지 않은 경우
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}

		String page = req.getParameter("page");
		int num = Integer.parseInt(req.getParameter("num"));
		JavaBoardDTO dto = dao.readBoard(num);

		if (dto == null) {
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		// 게시물을 올린 사용자나 admin이 아니면
		if (!dto.getUserId().equals(info.getUserId()) && !info.getUserId().equals("admin")) {
			resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
			return;
		}

		// bbsReply 테이블은 ON DELETE CASCADE 옵션으로 bbs 테이블의 데이터가 지워지면 자동 지워짐
		dao.deleteBoard(num);
		resp.sendRedirect(cp + "/bbs/list.do?page=" + page);
	}

	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리플 리스트 ---------------------------------------
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

		// 리스트에 출력할 데이터
		List<JavaReplyDTO> list = dao.listReply(num, start, end);

		// 엔터를 <br>
		Iterator<JavaReplyDTO> it = list.iterator();
		while (it.hasNext()) {
			JavaReplyDTO dto = it.next();
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}

		// 페이징처리(인수2개 짜리 js로 처리)
		String paging = util.paging(current_page, total_page);

		req.setAttribute("list", list);
		req.setAttribute("pageNo", current_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("total_page", total_page);
		req.setAttribute("paging", paging);

		// 포워딩
		String path = "/WEB-INF/views/bbs/listReply.jsp";
		forward(req, resp, path);
	}

	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리플 저장하기 ---------------------------------------
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		String state="true";
		if (info == null) { // 로그인되지 않은 경우
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

	private void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리플 삭제 ---------------------------------------
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		JavaBoardDAO dao = new JavaBoardDAO();
		
		int replyNum = Integer.parseInt(req.getParameter("replyNum"));
		String userId=req.getParameter("userId");
		
		String state="false";
		if (info == null) { // 로그인되지 않은 경우
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
