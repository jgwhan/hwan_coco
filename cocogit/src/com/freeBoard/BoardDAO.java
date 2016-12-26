package com.freeBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.util.DBConn;

public class BoardDAO {

	private Connection conn = DBConn.getConnection();
	
	public int insertBoard(BoardDTO dto, String mode){ // ±Û¾²±â,
		
		int result=0;
		PreparedStatement pstmt=null;
		String sql;		
		String fields="num,userid,subject,content"; 
		
		sql="INSERT INTO freeboard (" + fields + ") VALUES (freeboard_seq.NEXTVAL,?,?,?)";
		
		try {
			pstmt = conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			
			result = pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
}
