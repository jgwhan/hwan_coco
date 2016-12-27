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
		
		// session ��ü
		HttpSession session=req.getSession();
		
		MemberDAO dao=new MemberDAO();
		
		if(uri.indexOf("login.do")!=-1) {
			// �α��� ��
			forward(req, resp, "/WEB-INF/views/member/login.jsp");
							//  /WEB-INF/views/freeBoard/list.jsp
			
		} 
		else if(uri.indexOf("login_ok.do")!=-1) {
			String userId=req.getParameter("userId");
			String userPwd=req.getParameter("userPwd");
			
			MemberDTO dto=dao.readMember(userId);
			if(dto==null || ! dto.getUserPwd().equals(userPwd)) {
				req.setAttribute("message", "���̵� �Ǵ� �н����尡 ��ġ���� �ʽ��ϴ�.");
				forward(req, resp, "/WEB-INF/views/member/login.jsp");
				return;
			}
			
			// ���ǿ� �α��� ������ �����Ѵ�.
			SessionInfo info=new SessionInfo();
			info.setUserId(dto.getUserId());
			info.setUserName(dto.getUserName());
			
			session.setAttribute("member", info);
			
			// ����ȭ������ redirect
			resp.sendRedirect(cp);
			
			// �α��� ó��
		} else if(uri.indexOf("logout.do")!=-1) {
			// �α׾ƿ�ó��
			session.removeAttribute("member");
			session.invalidate();
			
			resp.sendRedirect(cp);
			
		} else if(uri.indexOf("member.do")!=-1) {
			// ȸ������ ��
			req.setAttribute("title", "ȸ�� ����");
			req.setAttribute("mode", "created");
			forward(req, resp, "/WEB-INF/views/member/member.jsp");
			
		}
		
		else if(uri.indexOf("member_ok.do")!=-1)
		{
			MemberDTO dto = new MemberDTO();

			dto.setUserId(req.getParameter("userId"));
			dto.setUserPwd(req.getParameter("userPwd"));
			dto.setUserName(req.getParameter("userName"));
			dto.setBirth(req.getParameter("birth"));
			dto.setEmail(req.getParameter("email"));
			
			String tel1 = req.getParameter("tel1");
			String tel2 = req.getParameter("tel2");
			String tel3 = req.getParameter("tel3");
			
			if (tel1 != null && tel1.length() != 0 && tel2 != null && tel2.length() != 0 && tel3 != null && tel3.length() != 0)
				dto.setTel(tel1 + "-" + tel2 + "-" + tel3);
			
			dto.setJob(req.getParameter("job"));
			dto.setZip(req.getParameter("zip"));
			dto.setAddr1(req.getParameter("addr1"));
			dto.setAddr2(req.getParameter("addr2"));

			int result = dao.insertMember(dto);
			
			System.out.println("�̱��� ��");
			
			if (result != 1)
			{
				String message = "ȸ�� ������ ���� �߽��ϴ�.";

				req.setAttribute("title", "ȸ�� ����");
				req.setAttribute("mode", "created");
				req.setAttribute("message", message);
				
				forward(req, resp, "/WEB-INF/views/member/member.jsp");
				
				return;
			}

			StringBuffer sb = new StringBuffer();
			
			sb.append("<b>"+dto.getUserName() + "</b>�� ȸ�������� �Ǿ����ϴ�.<br>");
			sb.append("����� ȸ�����԰� ���ÿ� 1,000,000�� ��ݵǼ̽��ϴ�. ���ϵ帳�ϴ�.<br>");
			sb.append("����ȭ������ �̵��Ͽ� �α��� �Ͻð� �߰� ��� ���ظ� �����ּ���.<br>");
			
			req.setAttribute("title", "ȸ�� ����");
			req.setAttribute("message", sb.toString());
			
			forward(req, resp, "/WEB-INF/views/member/complete.jsp");
		}

	}

}



