package com.freeBoard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

@WebServlet("/freeBoard/*")
public class FreeBoardServlet extends MyServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// TODO Auto-generated method stub
		MyUtil myutil = new MyUtil();

		req.setCharacterEncoding("UTF-8");
		String cp = req.getContextPath();
		String uri = req.getRequestURI();

		HttpSession session = req.getSession();

		BoardDAO dao = new BoardDAO();
	
		SessionInfo info = (SessionInfo) session.getAttribute("member");

		if (uri.indexOf("list.do") != -1) {
			forward(req, resp, "/WEB-INF/views/freeBoard/list.jsp");

		} else if (uri.indexOf("created.do") != -1) {
			
			// �� �ۼ� ��
//			System.out.println(info.getUserId()); ���ǵ������� test.
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
			// db�ӿ� ������ �߰�.
			// dto.setName(req.getParameter("name"));
			dto.setUserId(info.getUserId()); // id�� session�� ������ �ִ�.
			dto.setSubject(req.getParameter("subject"));
			dto.setContent(req.getParameter("content"));
			
			dao.insertBoard(dto, "created");

			resp.sendRedirect(cp + "/freeBoard/list.do");

		}
	}

}
