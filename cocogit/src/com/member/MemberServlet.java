package com.member;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.util.MyServlet;

@WebServlet("/member/*")
public class MemberServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		
		req.setCharacterEncoding("UTF-8");
		String uri=req.getRequestURI();
		String cp=req.getContextPath();
		
		// session 객체
		HttpSession session=req.getSession();
		
		MemberDAO dao=new MemberDAO();
		
		if(uri.indexOf("login.do")!=-1) {
			// 로그인 폼
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
			
		} else if(uri.indexOf("login_ok.do")!=-1) {
			String userId=req.getParameter("userId");
			String userPwd=req.getParameter("userPwd");
			
			MemberDTO dto=dao.readMember(userId);
			if(dto==null || ! dto.getUserPwd().equals(userPwd)) { //틀리면
				req.setAttribute("message", "아이디 또는 패스워드가 일치하지 않습니다.");
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			// 세션에 로그인 정보를 저장한다.
			SessionInfo info=new SessionInfo();
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());
			
			session.setAttribute("member", info);
			
			// 메인화면으로 redirect
			resp.sendRedirect(cp);
			
			// 로그인 처리
		} else if(uri.indexOf("logout.do")!=-1) {
			// 로그아웃처리
			session.removeAttribute("member");
			session.invalidate();
			
			resp.sendRedirect(cp);
			
		} else if(uri.indexOf("member.do")!=-1) {
			// 회원가입 폼
			req.setAttribute("title", "회원 가입");
			req.setAttribute("mode", "created");
			forward(req, resp, "/WEB-INF/views/member/member.jsp");
			
		} else if(uri.indexOf("member_ok.do")!=-1) {
			// 회원가입 처리
			MemberDTO dto = new MemberDTO();

			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			dto.setBirth(req.getParameter("birth"));
			dto.setEmail(req.getParameter("email"));
			String tel1 = req.getParameter("tel1");
			String tel2 = req.getParameter("tel2");
			String tel3 = req.getParameter("tel3");
			if (tel1 != null && tel1.length() != 0 && tel2 != null
					&& tel2.length() != 0 && tel3 != null && tel3.length() != 0) {
				dto.setTel(tel1 + "-" + tel2 + "-" + tel3);
			}
			
			dto.setJob(req.getParameter("job"));
			dto.setZip(req.getParameter("zip"));
			dto.setAddr1(req.getParameter("addr1"));
			dto.setAddr2(req.getParameter("addr2"));

			int result = dao.insertMember(dto);
			if (result != 1) {
				String message = "회원 가입이 실패 했습니다.";

				req.setAttribute("title", "회원 가입");
				req.setAttribute("mode", "created");
				req.setAttribute("message", message);
				forward(req, resp, "/WEB-INF/views/member/member.jsp");
				return;
			}

			StringBuffer sb=new StringBuffer();
			sb.append("<b>"+dto.getUserName() 
			   + "</b>님 회원가입이 되었습니다.<br>");
			sb.append("메인화면으로 이동하여 로그인 하시기 바랍니다.<br>");
			
			req.setAttribute("title", "회원 가입");
			req.setAttribute("message", sb.toString());
			
			forward(req, resp, "/WEB-INF/views/member/complete.jsp");
			
		}
		
		
	}

}



