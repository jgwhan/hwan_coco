package com.freeBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;

import com.util.DBConn;

public class BoardDAO {

	private Connection conn = DBConn.getConnection();
	
	public int insertBoard(BoardDTO dto){
		int result=0;
		PreparedStatement pstmt=null;
		String sql;
		
		String fields="num,userid,subject,content,hitcount";
		sql="INSERT INTO freeboard (" + fields + ") VALUES (freeboard_seq.NEXTVAL,?,?,?,?";
		
		
		try {
			pstmt = conn.prepareStatement(sql);
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return result;
	}
	
}
