package com.board;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;

public class BoardDAO {
	private Connection conn=DBConn.getConnection();
	
	public int insertBoard(BoardDTO dto, String mode) {
		int result=0;
		PreparedStatement pstmt=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("INSERT INTO board(");
			sb.append("   boardNum, userId, subject, ");
			sb.append("   content, groupNum, depth, ");
			sb.append("   orderNo, parent)");
			sb.append("   VALUES(?, ?, ?, ?, ?, ?, ?, ?)");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			int maxNum=maxBoardNum()+1;
			
			pstmt.setInt(1, maxNum);
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			if(mode.equals("created")) {
				pstmt.setInt(5, maxNum);
				pstmt.setInt(6, 0);
				pstmt.setInt(7, 0);
				pstmt.setInt(8, 0);
				
			} else if(mode.equals("reply")) {
				//답변인경우
				updateOrderNo(dto.getGroupNum(), dto.getOrderNo());
				
				dto.setDepth(dto.getDepth()+1);
				dto.setOrderNo(dto.getOrderNo()+1);
				
				pstmt.setInt(5, dto.getGroupNum());
				pstmt.setInt(6, dto.getDepth());
				pstmt.setInt(7, dto.getOrderNo());
				pstmt.setInt(8, dto.getParent());
				
				
			}
			
			result=pstmt.executeUpdate();
			
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	public int maxBoardNum() {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="SELECT NVL(MAX(boardNum), 0) FROM board";
			pstmt=conn.prepareStatement(sql);
			rs=pstmt.executeQuery();
			if(rs.next())
				result=rs.getInt(1);
			rs.close();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	public int dataCount() {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="SELECT COUNT(*) FROM board";
			pstmt=conn.prepareStatement(sql);
			
			rs=pstmt.executeQuery();
			
			if(rs.next())
				result=rs.getInt(1);
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;

	}

	public int dataCount(String searchKey, String searchValue) {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		
		try {
			sql="SELECT COUNT(*) FROM board b ";
			sql+="  JOIN  member1 m ON b.userId = m.userId ";
			sql+=" WHERE ";
			
			if(searchKey.equals("userName"))
				sql+="INSTR(userName, ?) = 1";
			else if(searchKey.equals("subject"))
				sql+="INSTR(subject, ?) >= 1";
			else if(searchKey.equals("content"))
				sql+="INSTR(content, ?) >= 1";
			else if(searchKey.equals("created"))
				sql+="TO_CHAR(created, 'YYYY-MM-DD') = ?";
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setString(1, searchValue);
			
			rs=pstmt.executeQuery();
			
			if(rs.next())
				result=rs.getInt(1);
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;

	}

	public List<BoardDTO> listBoard(int start, int end) {
		List<BoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT boardNum, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("            ,groupNum, orderNo, depth ");
			sb.append("        FROM board b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId ");
			sb.append("	      ORDER BY groupNum DESC, orderNo ASC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO dto=new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				
				list.add(dto);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	public List<BoardDTO> listBoard(int start, int end, String searchKey, String searchValue) {
		List<BoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT boardNum, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("            ,groupNum, orderNo, depth ");
			sb.append("        FROM board b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId ");
			
			sb.append("        WHERE ");
			if(searchKey.equals("userName"))
				sb.append("INSTR(userName, ?) = 1");
			else if(searchKey.equals("subject"))
				sb.append("INSTR(subject, ?) >= 1");
			else if(searchKey.equals("content"))
				sb.append("INSTR(content, ?) >= 1");
			else if(searchKey.equals("created"))
				sb.append("TO_CHAR(created, 'YYYY-MM-DD') = ?");
			
			sb.append("	      ORDER BY groupNum DESC, orderNo ASC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setString(1, searchValue);
			pstmt.setInt(2, end);
			pstmt.setInt(3, start);

			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				BoardDTO dto=new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("orderNo"));
				
				list.add(dto);
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return list;
	}
	
	public int updateHitCount(int boardNum) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql;
		try {
			sql="UPDATE board SET hitCount=hitCount+1";
			sql+="    WHERE boardNum=?";
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			result=pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	
	public BoardDTO readBoard(int boardNum) {
		BoardDTO dto=null;
		
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT boardNum, b.userId, userName, ");
			sb.append("subject, content, hitCount, created");
			sb.append(", groupNum, depth, orderNo, parent");
			sb.append("  FROM  board b");
			sb.append("  JOIN member1 m ON b.userId = m.userId ");
			sb.append("  WHERE boardNum=?");
			
			pstmt=conn.prepareStatement(sb.toString());
			pstmt.setInt(1, boardNum);
			rs=pstmt.executeQuery();
			
			if(rs.next()) {
				dto=new BoardDTO();
				
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setUserId(rs.getString("userId"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setGroupNum(rs.getInt("groupNum"));
				dto.setOrderNo(rs.getInt("orderNo"));
				dto.setDepth(rs.getInt("depth"));
				dto.setParent(rs.getInt("parent"));
			}
			rs.close();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return dto;
	}
	
    // 이전글
    public BoardDTO preReadBoard(int groupNum, int orderNo, String searchKey, String searchValue) {
        BoardDTO dto=null;

        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue!=null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT boardNum, subject  ");
    			sb.append("               FROM board b");
    			sb.append("               JOIN member1 m ON b.userId=m.userId");
    			if(searchKey.equals("created"))
    				sb.append("           WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ? ) AND ");
    			else if(searchKey.equals("userName"))
    				sb.append("           WHERE (INSTR(userName, ?) = 1 ) AND ");
    			else
    				sb.append("           WHERE (INSTR(" + searchKey + ", ?) >= 1 ) AND ");
    			
                sb.append("     (( groupNum = ? AND orderNo < ?) ");
                sb.append("         OR (groupNum > ? )) ");
                sb.append("         ORDER BY groupNum ASC, orderNo DESC) tb WHERE ROWNUM = 1 ");

                pstmt=conn.prepareStatement(sb.toString());
                
                pstmt.setString(1, searchValue);
                pstmt.setInt(2, groupNum);
                pstmt.setInt(3, orderNo);
                pstmt.setInt(4, groupNum);
			} else {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("     SELECT boardNum, subject FROM board b JOIN member1 m ON b.userId=m.userId ");                
                sb.append("  WHERE (groupNum = ? AND orderNo < ?) ");
                sb.append("         OR (groupNum > ? ) ");
                sb.append("         ORDER BY groupNum ASC, orderNo DESC) tb WHERE ROWNUM = 1 ");

                pstmt=conn.prepareStatement(sb.toString());
                pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
			}

            rs=pstmt.executeQuery();

            if(rs.next()) {
                dto=new BoardDTO();
                dto.setBoardNum(rs.getInt("boardNum"));
                dto.setSubject(rs.getString("subject"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }
    
        return dto;
    }

    // 다음글
    public BoardDTO nextReadBoard(int groupNum, int orderNo, String searchKey, String searchValue) {
        BoardDTO dto=null;

        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue!=null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT boardNum, subject ");
    			sb.append("               FROM board b");
    			sb.append("               JOIN member1 m ON b.userId=m.userId");
    			if(searchKey.equals("created"))
    				sb.append("           WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ? ) AND ");
    			else if(searchKey.equals("userName"))
    				sb.append("           WHERE (INSTR(userName, ?) = 1) AND ");
    			else
    				sb.append("           WHERE (INSTR(" + searchKey + ", ?) >= 1) AND ");
    			
                sb.append("     (( groupNum = ? AND orderNo > ?) ");
                sb.append("         OR (groupNum < ? )) ");
                sb.append("         ORDER BY groupNum DESC, orderNo ASC) tb WHERE ROWNUM = 1 ");

                pstmt=conn.prepareStatement(sb.toString());
                pstmt.setString(1, searchValue);
                pstmt.setInt(2, groupNum);
                pstmt.setInt(3, orderNo);
                pstmt.setInt(4, groupNum);

			} else {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("     SELECT boardNum, subject FROM board b JOIN member1 m ON b.userId=m.userId ");
                sb.append("  WHERE (groupNum = ? AND orderNo > ?) ");
                sb.append("         OR (groupNum < ? ) ");
                sb.append("         ORDER BY groupNum DESC, orderNo ASC) tb WHERE ROWNUM = 1 ");

                pstmt=conn.prepareStatement(sb.toString());
                pstmt.setInt(1, groupNum);
                pstmt.setInt(2, orderNo);
                pstmt.setInt(3, groupNum);
            }

            rs=pstmt.executeQuery();

            if(rs.next()) {
                dto=new BoardDTO();
                dto.setBoardNum(rs.getInt("boardNum"));
                dto.setSubject(rs.getString("subject"));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            System.out.println(e.toString());
        }

        return dto;
    }
	
	
	//답변인경우 orderNo변경
    
    public int updateOrderNo(int groupNum, int orderNo){
    int result=0;
    PreparedStatement pstmt = null;
    String sql;
    
    try {
		sql="UPDATE board SET orderNo = orderNo+1";
		sql+=" WHERE groupNum = ? AND orderNo > ?"; // groupnum이 같은데 큰놈들의 orderno을 +1씩 해준다.
		pstmt = conn.prepareStatement(sql);
		
		pstmt.setInt(1, groupNum);
		pstmt.setInt(2, orderNo);
		
		result = pstmt.executeUpdate();
		pstmt.close();
		
		
	} catch (Exception e) {
		System.out.println(e.toString());
	}
    
    
    return result;
    		
    }
    
    public int deleteBoard(int boardNum){ // 게시글 삭제.
    	int result = 0;
    	PreparedStatement pstmt = null;
    	String sql;
    	
    	
    	try {
    		sql="DELETE FROM board WHERE boardNum IN";  
    		sql+="(SELECT boardNum FROM board START WITH boardNUM=?";  
    		sql+=" CONNECT BY PRIOR boardNum = parent)";
    				
    		pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
    		result = pstmt.executeUpdate();
    		pstmt.close();
    		
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	
    	return  result;
    	
    }
    		
    
    public int updateBoard(BoardDTO dto){ // 게시글 삭제.
    	int result = 0;
    	PreparedStatement pstmt = null;
    	String sql;
    	
    	try {    		
        		 
    		sql="UPDATE board SET subject =?,content=? WHERE boardNum=?";
    				
    	    pstmt=conn.prepareStatement(sql);
    	    pstmt.setString(1, dto.getSubject());
    		pstmt.setString(2, dto.getContent());
    		pstmt.setInt(3, dto.getBoardNum());
    		
    		result = pstmt.executeUpdate();
    		pstmt.close();
    		
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	
    	return result;
    	
    }
    		
	
	
	
	
	
	
	
	
	
	
	
}
