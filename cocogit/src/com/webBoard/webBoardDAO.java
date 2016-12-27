package com.webBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.qaBoard.qaBoardDTO;
import com.util.DBConn;

public class webBoardDAO {
	private Connection conn=DBConn.getConnection();
	public int insertBoard(webBoardDTO dto) { 
		int result=0;
		PreparedStatement pstmt=null;
		StringBuffer sb = new StringBuffer();
		//String sql;
		try {
			//sql = "INSERT INTO board(boardnum, userid, subject, content, groupnum, depth, orderno, parent) VALUES(?,?,?,?,?,?,?,?)";
			sb.append("INSERT INTO webboard(");
			sb.append(" num, userId, subject,");
			sb.append(" content)  ");
			sb.append(" VALUES(?,?,?,?)");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			int maxNum=maxBoardNum()+1;
			pstmt.setInt(1, maxNum);
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
				
			
			
			result = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());	
		} finally {
			if(pstmt!=null) {
				try {
					pstmt.close();
					pstmt=null;
				} catch (Exception e2) {
				}
			}
		}
		return result;
	}
	
	public int maxBoardNum() {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql="SELECT NVL(MAX(num),0) FROM webboard"; // boardNum의 맥스값(최대값)
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next()) 
				result=rs.getInt(1);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
	
	
	public int dataCount() {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="SELECT COUNT(*) FROM webboard";
			pstmt=conn.prepareStatement(sql);
			
			rs=pstmt.executeQuery();
			if(rs.next())
				result=rs.getInt(1);
			
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
			pstmt=null;
			rs=null;
			
			return result;
	}
	
	public int dataCount(String searchKey, String searchValue) {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("SELECT COUNT(*) FROM webboard b");
			sb.append(" JOIN member1 m ON b.userId = m.userId ");
			sb.append(" WHERE ");
			if(searchKey.equals("userName"))
				sb.append("INSTR(userName,?)=1 ");
			else if(searchKey.equals("subject"))
				sb.append("INSTR(subject, ?)>= 1");
			else if(searchKey.equals("content")) 
				sb.append("INSTR(content,?)>=1");
			else if(searchKey.equals("created"))
				sb.append("TO_CHAR(created,'YYYY-MM-DD')=?");
				
				pstmt=conn.prepareStatement(sb.toString());
				pstmt.setString(1, searchValue);
				
				rs=pstmt.executeQuery();
			if(rs.next())
			{
				result=rs.getInt(1);
				System.out.println(result);
			}
			pstmt.close();
			System.out.println(result);
			
		} catch (Exception e) {
			System.out.println(e.toString());
		} finally {
			if(rs!=null) {
				try {
					rs.close();
				} catch (Exception e2) {
				}
			}
			if(pstmt!=null) {
				try {
					pstmt.close();
				} catch (Exception e2) {
					// TODO: handle exception
				}
			}
		}
			pstmt=null;
			rs=null;
			
			return result;
	}
	public List<webBoardDTO> listBoard(int start, int end) {
		List<webBoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("        FROM webboard b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId ");
			sb.append("	      ORDER BY num DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				webBoardDTO dto=new webBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				
				list.add(dto);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	
	public List<webBoardDTO> listBoard(int start, int end, String searchKey, String searchValue) {
		List<webBoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try { 
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("        FROM webboard b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId "); // 이름때문에 쪼인하였다.
			sb.append("        WHERE ");
			
			
			if(searchKey.equals("userName")) // 사용자가 이름으로 검색하고 있으면!!
				sb.append("INSTR(userName, ?) = 1"); // =1 --> 첫글자가 '김'일때 김으로시작하는 글자들 출력!
			else if(searchKey.equals("subject")) // 제목으로 검색 (DB의 컬럼명)
				sb.append("INSTR(subject, ?) >= 1"); // >=1 --> 첫글자든 뒷글자든 포함되면 검색된다
			else if(searchKey.equals("content"))
				sb.append("INSTR(content, ?) >= 1");
			else if(searchKey.equals("created"))
				sb.append("TO_CHAR(created, 'YYYY-MM-DD') = ?");
			
			sb.append("	      ORDER BY num DESC ");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				webBoardDTO dto=new webBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				
				list.add(dto);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	
}
