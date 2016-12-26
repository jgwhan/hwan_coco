package com.guestBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class GuestDAO {
	
	private Connection conn=DBConn.getConnection();
	
	public int insertGuest(GuestDTO dto)
	{
		int result=0;
		PreparedStatement pstmt=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("INSERT INTO guestboard(num, userId, content) ");
			sb.append("VALUES(guestboard_seq.NEXTVAL, ?, ?)");
			
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getContent());
			
			result=pstmt.executeUpdate();
			pstmt.close();
			pstmt=null;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	public int dataCount()
	{
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="SELECT NVL(COUNT(*), 0) FROM guestboard";
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next())
				result=rs.getInt(1);
			rs.close();
			pstmt.close();
			
			rs=null;
			pstmt=null;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	public List<GuestDTO> listGuest(int start, int end) {
		List<GuestDTO> list=new ArrayList<GuestDTO>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, g.userId, userName, content");
			sb.append("                ,created ");
			sb.append("         FROM guestboard g JOIN member1 m ON g.userId=m.userId ");
			sb.append("	       ORDER BY num DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);
			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				GuestDTO dto=new GuestDTO();
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
				dto.setCreated(rs.getString("created"));
				
				list.add(dto);
			}
			
			rs.close();
			pstmt.close();
			rs=null;
			pstmt=null;
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	public int deleteGuest(int num) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			sql="DELETE FROM guestboard WHERE num=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result=pstmt.executeUpdate();
			
			pstmt.close();
			pstmt=null;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	
}
