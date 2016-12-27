package com.freePhoto;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;


import com.util.DBConn;

public class FreePhotoDAO {
	private Connection conn = DBConn.getConnection();

	public int insertPhoto(FreePhotoDTO dto) {
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		try {
			sql = "INSERT INTO photoboard(num, userId, subject";
			sql += ", content, imageFilename) VALUES (";
			sql += "photoboard_seq.NEXTVAL, ?, ?, ?, ?)";

			pstmt = conn.prepareStatement(sql);

			pstmt.setString(1, dto.getUserId());
			pstmt.setString(2, dto.getSubject());
			pstmt.setString(3, dto.getContent());
			pstmt.setString(4, dto.getImageFilename());

			result = pstmt.executeUpdate();

			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}

	public int dataCount() {
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;

		try {
			sql = "SELECT COUNT(*) FROM photoboard";
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

	public List<FreePhotoDTO> listPhoto(int start, int end) {
		List<FreePhotoDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, userName, subject, imageFilename");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created");
			sb.append("        FROM photoboard p ");
			sb.append("        JOIN member1 m ON p.userId = m.userId ");
			sb.append("	      ORDER BY num DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();

			while (rs.next()) {
				FreePhotoDTO dto = new FreePhotoDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setImageFilename(rs.getString("imageFilename"));
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

	public FreePhotoDTO readPhoto(int num) {
		FreePhotoDTO dto = null;

		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();

		try {
			sb.append("SELECT num, p.userId, userName, ");
			sb.append("subject, content, IMAGEFILENAME, created");
			sb.append("  FROM  photoboard p");
			sb.append("  JOIN member1 m ON p.userId = m.userId ");
			sb.append("  WHERE num=?");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);
			rs = pstmt.executeQuery();

			if (rs.next()) {
				dto = new FreePhotoDTO();

				dto.setNum(rs.getInt("num"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setImageFilename(rs.getString("IMAGEFILENAME"));
				dto.setCreated(rs.getString("created"));

			}
			rs.close();
			pstmt.close();

		} catch (Exception e) {
			System.out.println(e.toString());
		}

		return dto;
	}

	
	
	public int deletePhoto(int num) {
		// TODO Auto-generated method stub
		int result=0;
	      PreparedStatement pstmt = null;
	      String sql;
	      
	      try {
	         sql="DELETE FROM photoboard WHERE num=?";
	         pstmt=conn.prepareStatement(sql);
	         pstmt.setInt(1, num);
	         
	         result = pstmt.executeUpdate();
	         pstmt.close();
	         
	      } catch (Exception e) {
	         System.out.println(e.toString());
	      }
	      return result;
	}
	
	public int updatePhoto(FreePhotoDTO dto) {
    	int result=0;
    	PreparedStatement pstmt=null;
    	String sql;
    	
    	try {
			sql="UPDATE photoboard SET subject=?, content=?, imageFilename=?";
			sql+="  WHERE num=?";
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setString(3, dto.getImageFilename());
			pstmt.setInt(4, dto.getNum());
			
			result=pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	return result;
    }

	
}
