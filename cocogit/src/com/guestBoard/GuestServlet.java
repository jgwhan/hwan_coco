package com.guestBoard;

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

@WebServlet("/guestBoard/*")
public class GuestServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.setCharacterEncoding("utf-8");
		
		String uri = req.getRequestURI();
		
		// uri�� ���� �۾� ����
		if(uri.indexOf("list.do")!=-1) {
			guest(req, resp);
		} else if(uri.indexOf("guest_ok.do")!=-1) {
			guestSubmit(req, resp);
		} else if(uri.indexOf("delete.do")!=-1) {
			delete(req, resp);
		}
	}

	private void guest(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		// ���� ����Ʈ
		String cp = req.getContextPath();
		
		GuestDAO dao = new GuestDAO();
		MyUtil util = new MyUtil();
		
		// �Ѿ�� ������
		String page = req.getParameter("page");
		int current_page = 1;
		
		if(page != null && page.length() != 0)
			current_page = Integer.parseInt(page);
		
		// ��ü ������ ����
		int dataCount = dao.dataCount();
		
		// ��ü�������� ���ϱ�
		int numPerPage = 5;
		int total_page = util.pageCount(numPerPage, dataCount);
		
		// ��ü���������� ǥ���� �������� ū���
		if(total_page < current_page)
			current_page = total_page;
		
		// �����õ������� ���۰� ��
		int start = (current_page - 1) * numPerPage + 1;
		int end = current_page * numPerPage;
		
		// ������ ��������
		List<GuestDTO> list = dao.listGuest(start, end);
		
		Iterator<GuestDTO> it = list.iterator();
		while (it.hasNext())
		{
			GuestDTO dto = it.next();
			
			dto.setContent(dto.getContent().replaceAll(">", "&gt;"));
			dto.setContent(dto.getContent().replaceAll("<", "&lt;"));
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}
		
		// ����¡ó��
		String strUrl = cp + "/guestBoard/list.do";
		String paging = util.paging(current_page, total_page, strUrl);
		
		// guest.jsp�� �Ѱ��� ������
		req.setAttribute("list", list);
		req.setAttribute("page", current_page);
		req.setAttribute("paging", paging);
		req.setAttribute("dataCount", dataCount);
		
		if(info == null) // �α��ε��� ���� ���
		{ 
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		forward(req, resp, "/WEB-INF/views/guestBoard/list.jsp");
	}
	
	private void guestSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		// ���� ����
		HttpSession session = req.getSession();
		SessionInfo info = (SessionInfo)session.getAttribute("member");
		
		String cp = req.getContextPath();
		
		if(info == null)	// �α��ε��� ���� ���
		{ 
			resp.sendRedirect(cp + "/member/login.do");
			return;
		}
		
		GuestDAO dao = new GuestDAO();
		GuestDTO dto = new GuestDTO();
		
		dto.setUserId(info.getUserId());
		dto.setContent(req.getParameter("content"));
		
		dao.insertGuest(dto);
		
		resp.sendRedirect(cp+"/guestBoard/list.do");
	}
	
	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� ����
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		
		String cp=req.getContextPath();
		
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		GuestDAO dao=new GuestDAO();
		
		int num=Integer.parseInt(req.getParameter("num"));
		String page=req.getParameter("page");
		String uid=req.getParameter("uid");

		if(! uid.equals(info.getUserId()) && ! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/guestBoard/list.do?page="+page);
			return;
		}

		dao.deleteGuest(num);
		
		resp.sendRedirect(cp+"/guestBoard/list.do?page="+page);
	}
}
