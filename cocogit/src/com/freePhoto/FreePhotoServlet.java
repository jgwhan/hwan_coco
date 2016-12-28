package com.freePhoto;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.member.SessionInfo;
import com.oreilly.servlet.MultipartRequest;
import com.oreilly.servlet.multipart.DefaultFileRenamePolicy;
import com.util.FileManager;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/freePhoto/*")
public class FreePhotoServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	private SessionInfo info;
	private String pathname;
	
	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri=req.getRequestURI();
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		
		info=(SessionInfo)session.getAttribute("member");
		
		if(info==null) {
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		// 이미지 파일 저장 경로
		String root=session.getServletContext().getRealPath("/");
		pathname=root+File.separator+"uploads"+
				      File.separator+"freePhoto";
		
		File f=new File(pathname);
		if(! f.exists())
			f.mkdirs();
		
		if(uri.indexOf("list.do")!=-1) {
			list(req, resp);
		} else if(uri.indexOf("created.do")!=-1) {
			created(req, resp);
		} else if(uri.indexOf("created_ok.do")!=-1) {
			created_ok(req, resp);
		} else if(uri.indexOf("article.do")!=-1) {
			article(req, resp);
		} else if(uri.indexOf("update.do")!=-1) {
			update(req, resp);
		} else if(uri.indexOf("update_ok.do")!=-1) {
			update_ok(req, resp);
		} else if(uri.indexOf("delete.do")!=-1) {
			delete(req, resp);
		}else if (uri.indexOf("listReply.do") != -1) {
			listReply(req, resp);
		} else if (uri.indexOf("insertReply.do") != -1) {
			insertReply(req, resp);
		} else if (uri.indexOf("deleteReply.do") != -1) {
			deleteReply(req, resp);
		}
		
	}
	
	private void list(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		MyUtil util=new MyUtil();
		String cp=req.getContextPath();
		FreePhotoDAO dao=new FreePhotoDAO();
		
		String page=req.getParameter("page");
		int current_page=1;
		if(page!=null)
			current_page=Integer.parseInt(page);
		
		int dataCount;
		int numPerPage = 6;
		int total_page;
		
		dataCount=dao.dataCount();
		
		total_page = util.pageCount(numPerPage, dataCount);
		if(current_page>total_page)
			current_page=total_page;
		
		int start = (current_page-1)*numPerPage+1;
		int end=current_page*numPerPage;
		
		List<FreePhotoDTO> list=dao.listPhoto(start, end);
		
		String listUrl=cp+"/freePhoto/list.do";
		String articleUrl=cp+"/freePhoto/article.do?page="+current_page;
		
		String paging=util.paging(current_page, total_page, listUrl);
		
		// list.jsp 페이지에 넘길 데이터
		req.setAttribute("list", list);
		req.setAttribute("page", current_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("page", current_page);
		req.setAttribute("total_page", total_page);
		req.setAttribute("articleUrl", articleUrl);
		req.setAttribute("paging", paging);
		FreePhotoDTO dto=new FreePhotoDTO();
		dto.setUserId(info.getUserId());
	
		forward(req, resp, "/WEB-INF/views/freePhoto/list.jsp");
	}
	
	private void created(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		req.setAttribute("mode", "created");
		forward(req, resp, "/WEB-INF/views/freePhoto/created.jsp");
	}
	
	private void created_ok(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String cp=req.getContextPath();
		
		String encType="UTF-8";
		int maxSize=15*1024*1024;
		MultipartRequest mreq=
				new MultipartRequest(req, pathname, maxSize,
						encType, new DefaultFileRenamePolicy());
		
		String saveFilename=mreq.getFilesystemName("upload");
		saveFilename=FileManager.doFilerename(pathname, saveFilename);
		
		FreePhotoDAO dao=new FreePhotoDAO();
		FreePhotoDTO dto=new FreePhotoDTO();
		
		dto.setUserId(info.getUserId());
		dto.setSubject(mreq.getParameter("subject"));
		dto.setContent(mreq.getParameter("content"));
		dto.setImageFilename(saveFilename);
		
		dao.insertPhoto(dto);
		
		resp.sendRedirect(cp+"/freePhoto/list.do");
	}
	
	private void article(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		
		
		String cp=req.getContextPath();
		FreePhotoDAO dao=new FreePhotoDAO();
		
	
		// 게시물 보기
		if(info==null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		
		// 게시물번호,페이지번호,rows [,searchKey,searchVlue]
		int num=Integer.parseInt(req.getParameter("num"));
		String page=req.getParameter("page");
		String rows=req.getParameter("rows");
	

		dao.updateHitCount(num);
		// 게시물 가져오기
		FreePhotoDTO dto=dao.readPhoto(num);
		if(dto==null) { // 게시물이 없으면 다시 리스트로
			resp.sendRedirect(cp+"/freePhoto/list.do?page="+page+"&rows="+rows);
			return;
		}
		
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		
		
		
		
		// 리스트나 이전글/다음글에서 사용할 파라미터
		String params="page="+page+"&rows="+rows;
		
		
		// JSP로 전달할 속성
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("rows", rows);
		req.setAttribute("params", params);
		
		
		
		forward(req, resp, "/WEB-INF/views/freePhoto/article.jsp");
	}	
	
	private void update(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String cp=req.getContextPath();
		FreePhotoDAO dao=new FreePhotoDAO();
		
		if(info==null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		
		int num=Integer.parseInt(req.getParameter("num"));
		
		String page=req.getParameter("page");
		String rows=req.getParameter("rows");
		
		FreePhotoDTO dto=dao.readPhoto(num);
		
		if(dto==null || ! dto.getUserId().equals(info.getUserId())) {
			resp.sendRedirect(cp+"/freePhoto/list.do?page="+page+"&rows="+rows);
			return;
		}
		
		req.setAttribute("dto", dto);
		req.setAttribute("mode", "update");
		req.setAttribute("num", num);
		req.setAttribute("page", page);
		req.setAttribute("rows", rows);
		
		
		forward(req, resp, "/WEB-INF/views/freePhoto/created.jsp");
	}	

	private void update_ok(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		PrintWriter out = resp.getWriter();
		String encType="UTF-8";
		int maxSize=15*1024*1024;
		MultipartRequest mreq=
				new MultipartRequest(req, pathname, maxSize,
						encType, new DefaultFileRenamePolicy());
		String saveFilename=mreq.getFilesystemName("upload");
		saveFilename=FileManager.doFilerename(pathname, saveFilename);
		if(info==null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		System.out.println(saveFilename);
		if(saveFilename!=null){
		
		
		
		String cp=req.getContextPath();
		FreePhotoDAO dao=new FreePhotoDAO();
		

		
		FreePhotoDTO dto=new FreePhotoDTO();
		dto.setNum(Integer.parseInt(mreq.getParameter("num")));
		dto.setSubject(mreq.getParameter("subject"));
		dto.setContent(mreq.getParameter("content"));
		dto.setImageFilename(saveFilename);
		String page=mreq.getParameter("page");
		String rows=mreq.getParameter("rows");
		
		
		
		
		dao.updatePhoto(dto);
		
		
		resp.sendRedirect(cp+"/freePhoto/list.do?page="+page+"&rows="+rows);
		}else
			out.println("<script>alert('이미지 확인 하세요')</script>");
	}
	
	
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		String cp=req.getContextPath();
		FreePhotoDAO dao=new FreePhotoDAO();
		
		if(info==null) {
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			return;
		}
		
		int num=Integer.parseInt(req.getParameter("num"));
		String page=req.getParameter("page");
		String rows=req.getParameter("rows");
		
		FreePhotoDTO dto=dao.readPhoto(num);
		if(dto!=null && (info.getUserId().equals("admin") || dto.getUserId().equals(info.getUserId())) ) {
			dao.deletePhoto(num);
		}
		
		
		resp.sendRedirect(cp+"/freePhoto/list.do?page="+page+"&rows="+rows);
	}
	
	
	
	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리플 리스트 ---------------------------------------
		FreePhotoDAO dao = new FreePhotoDAO();
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
		//List<PhotoReplyDTO> list = dao.listReply(num, start, end);
		List<PhotoReplyDTO> list = dao.listReply(num, start, end);
		// 엔터를 <br>
		Iterator<PhotoReplyDTO> it = list.iterator();
		while (it.hasNext()) {
			PhotoReplyDTO dto = it.next();
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
		String path = "/WEB-INF/views/freePhoto/listReply.jsp";
		forward(req, resp, path);
	}

	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// 리플 저장하기 ---------------------------------------
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		FreePhotoDAO dao = new FreePhotoDAO();
		
		String state="true";
		if (info == null) { // 로그인되지 않은 경우
			state="loginFail";
		} else {
			int num = Integer.parseInt(req.getParameter("num"));
			PhotoReplyDTO dto = new PhotoReplyDTO();
			dto.setNum(num);
			dto.setUserId(info.getUserId());
			dto.setContent(req.getParameter("content"));

			int result=dao.insertReply(dto);
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

		FreePhotoDAO dao = new FreePhotoDAO();
		
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




