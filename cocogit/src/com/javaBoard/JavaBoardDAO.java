package com.javaBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class JavaBoardDAO {
	private Connection conn = DBConn.getConnection();
	
	// 데이터 추가하기 
	public int insertBoard(JavaBoardDTO dto) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("INSERT INTO javaboard(num, userId, subject, content) ");
			sb.append(" VALUES (javaboard_seq.NEXTVAL, ?, ?, ?)");
			
			pstmt = conn.prepareStatement(sb.toString());
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
	
	// 데이터 갯수 세어오기 
	public int dataCount() 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select NVL(count(*), 0) from javaboard";
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

	// 검색했을 때의 데이터 개수 세어오기 
	public int dataCount(String searchKey, String searchValue) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "select NVL(count(*), 0)  from javaboard j JOIN member1 m ON j.userId=m.userId ";
			if(searchKey.equals("userName"))
				sql += "  where INSTR(userName, ?) = 1 ";
			else if(searchKey.equals("created"))
				sql += "  where TO_CHAR(created, 'YYYY-MM-DD') = ? ";
			else
				sql += "  where INSTR(" + searchKey+ ", ?) >= 1 ";
			
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);
			
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
	
	// 리스트 보여주기 
	public List<JavaBoardDTO> listBoard(int start, int end) 
	{
		List<JavaBoardDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, j.userId, userName, subject");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created");
			sb.append("            ,hitCount");
			sb.append("            FROM javaboard j JOIN member1 m ON j.userId=m.userId  ");
			sb.append("	       ORDER BY num DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs = pstmt.executeQuery();
			
			while(rs.next()) {
				JavaBoardDTO dto = new JavaBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
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
	
	// 검색했을 때의  리스트 보여주기 
	public List<JavaBoardDTO> listBoard(int start, int end, String searchKey, String searchValue) 
	{
		List<JavaBoardDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT num, j.userId, userName, subject");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created");
			sb.append("            ,hitCount");
			sb.append("            FROM javaboard j JOIN member1 m ON j.userId=m.userId ");
			if(searchKey.equals("userName"))
				sb.append("        WHERE  INSTR(userName, ?) = 1  ");
			else if(searchKey.equals("created"))
				sb.append("        WHERE TO_CHAR(created, 'YYYY-MM-DD') = ?  ");
			else
				sb.append("        WHERE INSTR(" + searchKey + ", ?) >= 1 ");
			sb.append("	       ORDER BY num DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			
			while(rs.next()) 
			{
				JavaBoardDTO dto = new JavaBoardDTO();
				
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
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
	
	
	// 조회수 증가시키기 
	public int updateHitCount(int num)  
	{
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		try {
			sql = "UPDATE javaboard SET hitCount=hitCount+1  WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	// 해당 게시물 내용 읽어오기 
	public JavaBoardDTO readBoard(int num) 
	{
		JavaBoardDTO dto = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("SELECT num, j.userId, userName, subject, content");
			sb.append("   ,created, hitCount ");
			sb.append("   FROM javaboard j JOIN member1 m ON j.userId=m.userId  ");
			sb.append("   WHERE num = ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);

			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				dto = new JavaBoardDTO();
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
			}
			
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
    // 이전글 보기 
    public JavaBoardDTO preReadBoard(int num, String searchKey, String searchValue) 
    {
        JavaBoardDTO dto = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue != null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT num, subject FROM javaboard j JOIN member1 m ON j.userId=m.userId ");
                if(searchKey.equals("userName"))
                	sb.append("     WHERE (INSTR(userName, ?) = 1)  ");
                else if(searchKey.equals("created"))
                	sb.append("     WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ?) ");
                else
                	sb.append("     WHERE (INSTR(" + searchKey + ", ?) >= 1) ");
                sb.append("         AND (num > ? ) ");
                sb.append("         ORDER BY num ASC ");
                sb.append("      ) tb WHERE ROWNUM=1 ");

                pstmt = conn.prepareStatement(sb.toString());
                pstmt.setString(1, searchValue);
                pstmt.setInt(2, num);
			} else {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT num, subject FROM javaboard j JOIN member1 m ON j.userId=m.userId ");
                sb.append("     WHERE num > ? ");
                sb.append("         ORDER BY num ASC ");
                sb.append("      ) tb WHERE ROWNUM=1 ");

                pstmt = conn.prepareStatement(sb.toString());
                pstmt.setInt(1, num);
			}

            rs=pstmt.executeQuery();

            if(rs.next()) {
                dto = new JavaBoardDTO();
                dto.setNum(rs.getInt("num"));
                dto.setSubject(rs.getString("subject"));
            }
            
            rs.close();
            pstmt.close();
            
        } catch (Exception e) {
            System.out.println(e.toString());
		} 
        
        return dto;
    }

    // 다음글 보기 
    public JavaBoardDTO nextReadBoard(int num, String searchKey, String searchValue) 
    {
        JavaBoardDTO dto = null;

        PreparedStatement pstmt = null;
        ResultSet rs = null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue != null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT num, subject FROM javaboard j JOIN member1 m ON j.userId=m.userId ");
                if(searchKey.equals("userName"))
                	sb.append("     WHERE (INSTR(userName, ?) = 1)  ");
                else if(searchKey.equals("created"))
                	sb.append("     WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ?) ");
                else
                	sb.append("     WHERE (INSTR(" + searchKey + ", ?) >= 1) ");
                sb.append("         AND (num < ? ) ");
                sb.append("         ORDER BY num DESC ");
                sb.append("      ) tb WHERE ROWNUM=1 ");

                pstmt = conn.prepareStatement(sb.toString());
                pstmt.setString(1, searchValue);
                pstmt.setInt(2, num);
			} else {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT num, subject FROM javaboard j JOIN member1 m ON j.userId=m.userId ");
                sb.append("     WHERE num < ? ");
                sb.append("         ORDER BY num DESC ");
                sb.append("      ) tb WHERE ROWNUM=1 ");

                pstmt = conn.prepareStatement(sb.toString());
                pstmt.setInt(1, num);
            }

            rs=pstmt.executeQuery();

            if(rs.next()) {
                dto = new JavaBoardDTO();
                dto.setNum(rs.getInt("num"));
                dto.setSubject(rs.getString("subject"));
            }
            
            rs.close();
            pstmt.close();
            
        } catch (Exception e) {
            System.out.println(e.toString());
		}
        
        return dto;
    }
	
    // 수정하기 
	public int updateBoard(JavaBoardDTO dto) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		sql="UPDATE javaboard SET subject=?, content=? WHERE num=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getNum());
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	// 삭제하기 
	public int deleteBoard(int num) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		sql="DELETE FROM javaboard WHERE num=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	// 리플 데이터 추가시키기 
	public int insertReply(JavaReplyDTO dto) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("INSERT INTO javaboardReply(replyNum, num, userId, content) ");
			sb.append(" VALUES (javaboardReply_seq.NEXTVAL, ?, ?, ?)");
			
			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, dto.getNum());
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getContent());
			
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	// 리플 갯수 읽어오기 
	public int dataCountReply(int num) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		String sql;
		
		try {
			sql = "SELECT NVL(COUNT(*), 0) FROM javaboardReply WHERE num=?";
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, num);
			
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

	// 리플 목록 읽어오기 
	public List<JavaReplyDTO> listReply(int num, int start, int end) 
	{
		List<JavaReplyDTO> list = new ArrayList<>();
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		StringBuffer sb = new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT replyNum, num, j.userId, userName, content");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created");
			sb.append("            FROM javaboardReply j JOIN member1 m ON j.userId=m.userId  ");
			sb.append("            WHERE num=?");
			sb.append("	       ORDER BY replyNum DESC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, num);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs = pstmt.executeQuery();
			
			while(rs.next()) 
			{
				JavaReplyDTO dto=new JavaReplyDTO();
				
				dto.setReplyNum(rs.getInt("replyNum"));
				dto.setNum(rs.getInt("num"));
				dto.setUserId(rs.getString("userId"));
				dto.setUserName(rs.getString("userName"));
				dto.setContent(rs.getString("content"));
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

	// 리플 삭제하기 
	public int deleteReply(int replyNum) 
	{
		int result = 0;
		PreparedStatement pstmt = null;
		String sql;
		
		sql="DELETE FROM javaboardReply WHERE replyNum=?";
		try {
			pstmt = conn.prepareStatement(sql);
			pstmt.setInt(1, replyNum);
			result = pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		} 
		
		return result;
	}
	
}
