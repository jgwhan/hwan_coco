package com.freeBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class BoardDAO {

	private Connection conn = DBConn.getConnection();
	
	public int insertBoard(BoardDTO dto, String mode){ // 글쓰기,
		
		int result=0;
		PreparedStatement pstmt=null;
		String sql;		
		String fields="num,userid,subject,content"; 
		
		sql="INSERT INTO freeboard (" + fields + ") VALUES (?,?,?,?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			int maxNum = maxBoardNum() + 1;
			
			pstmt.setInt(1, maxNum);
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	
	}
	
	public int maxBoardNum() {  //num계산.
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT NVL(MAX(num),0) FROM freeboard";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			if (rs.next())
				result = rs.getInt(1);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}
	
	public int dataCount() { //총 데이터 갯수 카운트./
		
		int result = 0;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT count(*) from freeboard";
			pstmt = conn.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next())
				result = rs.getInt(1);
			
			rs.close();
			pstmt.close();
				
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return result;
	}
	
	public List<BoardDTO> listBoard(int start, int end) {
		
		List<BoardDTO> list = new ArrayList<>();
				 
		 PreparedStatement pstmt = null;
		 ResultSet rs = null;
		 StringBuffer sb = new StringBuffer();
		 
		 try {
			 
				sb.append("SELECT * FROM (");
				sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
				sb.append("        SELECT num, userName, subject, hitCount");
				sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
				sb.append("");
				sb.append("        FROM freeboard b  ");
				sb.append("        JOIN member1 m ON b.userId=m.userId ");
				sb.append("	      ORDER BY Num DESC");
				sb.append("    ) tb WHERE ROWNUM <= ? ");
				sb.append(") WHERE rnum >= ? ");
				System.out.println(sb.toString());
	            
	            pstmt=conn.prepareStatement(sb.toString()); // 쿼리문을 넘김
	            pstmt.setInt(1, end); // 끝번호
	            pstmt.setInt(2, start); // 시작번호
			
	            rs=pstmt.executeQuery();
				
				while(rs.next()) {
					BoardDTO dto=new BoardDTO();
					
					dto.setNum(rs.getInt("Num"));
					dto.setUserName(rs.getString("userName"));
					dto.setSubject(rs.getString("subject"));
					dto.setHitCount(rs.getInt("hitCount"));
					dto.setCreated(rs.getString("created"));
//					dto.setGroupNum(rs.getInt("groupNum"));
//					dto.setDepth(rs.getInt("depth"));
//					dto.setOrderNo(rs.getInt("orderNo"));
					
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
