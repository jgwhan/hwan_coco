package com.qaBoard;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import com.util.DBConn;


public class qaBoardDAO {
	private Connection conn=DBConn.getConnection();
	
	public int insertBoard(qaBoardDTO dto, String mode) { 
		int result=0;
		PreparedStatement pstmt=null;
		StringBuffer sb = new StringBuffer();
		//String sql;
		try {
			//sql = "INSERT INTO board(boardnum, userid, subject, content, groupnum, depth, orderno, parent) VALUES(?,?,?,?,?,?,?,?)";
			sb.append("INSERT INTO qnaboard(");
			sb.append(" boardNum, userId, subject,");
			sb.append(" content, groupNum, depth, ");
			sb.append(" orderNo, parent) ");
			sb.append(" VALUES(?,?,?,?,?,?,?,?)");
			
			pstmt=conn.prepareStatement(sb.toString());
			
			int maxNum=maxBoardNum()+1;
			pstmt.setInt(1, maxNum);
			
			pstmt.setString(2, dto.getUserId());
			pstmt.setString(3, dto.getSubject());
			pstmt.setString(4, dto.getContent());
			
			
			if(mode.equals("created")) {
				pstmt.setInt(5, maxNum); // �׷�! �Խù� ������ ����!!
				pstmt.setInt(6, 0);
				pstmt.setInt(7, 0);
				pstmt.setInt(8, 0);
			}else if(mode.equals("reply")) {
				// �亯�� ���
				updateOrderNo(dto.getGroupNum(), dto.getOrderNo());
				
				dto.setDepth(dto.getDepth()+1);
				dto.setOrderNo(dto.getOrderNo()+1);
				
				pstmt.setInt(5, dto.getGroupNum()); 
				pstmt.setInt(6, dto.getDepth());
				pstmt.setInt(7, dto.getOrderNo());
				pstmt.setInt(8, dto.getParent());
			}
			
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
			sql="SELECT NVL(MAX(boardNum),0) FROM qnaboard"; // boardNum�� �ƽ���(�ִ밪)
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
			sql="SELECT COUNT(*) FROM qnaboard";
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
	
	
	
	
	public List<qaBoardDTO> listBoard(int start, int end) {
		List<qaBoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try {
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT boardNum, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("            ,groupNum, orderNo, depth ");
			sb.append("        FROM qnaboard b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId ");
			sb.append("	      ORDER BY groupNum DESC, orderNo ASC");
			sb.append("    ) tb WHERE ROWNUM <= ? ");
			sb.append(") WHERE rnum >= ? ");

			pstmt = conn.prepareStatement(sb.toString());
			pstmt.setInt(1, end);
			pstmt.setInt(2, start);

			rs=pstmt.executeQuery();
			
			while(rs.next()) {
				qaBoardDTO dto=new qaBoardDTO();
				
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
	
	
	public List<qaBoardDTO> listBoard(int start, int end, String searchKey, String searchValue) {
		List<qaBoardDTO> list=new ArrayList<>();
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb=new StringBuffer();
		
		try { 
			sb.append("SELECT * FROM (");
			sb.append("    SELECT ROWNUM rnum, tb.* FROM (");
			sb.append("        SELECT boardNum, userName, subject, hitCount");
			sb.append("            ,TO_CHAR(created, 'YYYY-MM-DD') created ");
			sb.append("            ,groupNum, orderNo, depth ");
			sb.append("        FROM qnaboard b  ");
			sb.append("        JOIN member1 m ON b.userId=m.userId "); // �̸������� �����Ͽ���.
			sb.append("        WHERE ");
			if(searchKey.equals("userName")) // ����ڰ� �̸����� �˻��ϰ� ������!!
				sb.append("INSTR(userName, ?) = 1"); // =1 --> ù���ڰ� '��'�϶� �����ν����ϴ� ���ڵ� ���!
			else if(searchKey.equals("subject")) // �������� �˻� (DB�� �÷���)
				sb.append("INSTR(subject, ?) >= 1"); // >=1 --> ù���ڵ� �ޱ��ڵ� ���ԵǸ� �˻��ȴ�
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
				qaBoardDTO dto=new qaBoardDTO();
				
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
	
	public int updateBoard(qaBoardDTO dto) { // �Խù� ����
		// TODO Auto-generated method stub
		int result=0;
		PreparedStatement pstmt=null;
		String sql;
		
		try {
			// ����, �̸�, ����, �н�����
			//subject, name,  content, pwd,
			// UPDATE ���̺��  SET �÷���= �����Ұ�[, �÷���= �����Ұ�, ...]  [WHERE ����];
			sql = "UPDATE qnaboard SET subject=?,content=? WHERE boardNum=?";
			///
			
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setString(1, dto.getSubject());
			pstmt.setString(2, dto.getContent());
			pstmt.setInt(3, dto.getBoardNum());
			
			result=pstmt.executeUpdate();
			pstmt.close();
			pstmt=null;
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return result;
	}
	
	public int dataCount(String searchKey, String searchValue) {
		int result=0;
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		StringBuffer sb = new StringBuffer();
		try {
			sb.append("SELECT COUNT(*) FROM qnaboard b");
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
	
	public int updateHitCount(int boardNum) {
		int result=0;
		PreparedStatement pstmt=null;
		String sql;
		try {
			sql="update qnaboard set hitCount=hitCount+1 where boardNum=?";
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			result=pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		return result;
	}
	public qaBoardDTO readBoard(int boardNum) {
		PreparedStatement pstmt=null;
		ResultSet rs=null;
		String sql;
		qaBoardDTO dto=null;
		try {
			System.out.println(boardNum);
			sql="select boardNum, userName, subject, content, hitCount, created, depth, ORDERNO, groupnum, parent, b.userid from qnaboard b JOIN member1 m ON b.userId = m.userId where boardNum=?";
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			rs=pstmt.executeQuery();
			
			if(rs.next()){
				dto = new qaBoardDTO();
				dto.setBoardNum(rs.getInt("boardNum"));
				dto.setUserName(rs.getString("userName"));
				dto.setSubject(rs.getString("subject"));
				dto.setContent(rs.getString("content"));
				dto.setHitCount(rs.getInt("hitCount"));
				dto.setCreated(rs.getString("created"));
				dto.setDepth(rs.getInt("depth"));
				dto.setOrderNo(rs.getInt("ORDERNO"));
				dto.setGroupNum(rs.getInt("groupnum"));
				dto.setParent(rs.getInt("parent"));
				dto.setUserId(rs.getString("userid"));
			}
			rs.close();
			pstmt.close();
			rs=null;
			pstmt=null;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e.toString());
		}
		return dto;
	}
	
	
	
	 // ������
    public qaBoardDTO preReadBoard(int groupNum, int orderNo, String searchKey, String searchValue) {
        qaBoardDTO dto=null;

        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue!=null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT boardNum, subject  ");
    			sb.append("               FROM qnaboard b");
    			sb.append("               JOIN member1 m ON b.userId=m.userId");
    			if(searchKey.equals("created"))
    				sb.append("           WHERE (TO_CHAR(created, 'YYYY-MM-DD') = ? ) AND ");
    			else if(searchKey.equals("userName"))
    				sb.append("           WHERE (INSTR(userName, ?) = 1 ) AND ");
    			else
    				sb.append("           WHERE (INSTR(" + searchKey + ", ?) >= 1 ) AND ");
    			
                sb.append("     (( groupNum = ? AND orderNo < ?) "); // �׷��ȣ�� �����鼭 orderNo�� ������ �߿���
                sb.append("         OR (groupNum > ? )) "); // �װ� �ƴϸ� �׷��ȣ�� �ٸ��� �׷��ȣ�� �� ū�͵� �߿���
                sb.append("         ORDER BY groupNum ASC, orderNo DESC) tb WHERE ROWNUM = 1 ");
                // asc -> �׷��ȣ�� ū���� ���� ������(��������), ������ȣ�� �������߿� ���� ū��(��������)

                pstmt=conn.prepareStatement(sb.toString());
                
                pstmt.setString(1, searchValue);
                pstmt.setInt(2, groupNum);
                pstmt.setInt(3, orderNo);
                pstmt.setInt(4, groupNum);
			} else {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("     SELECT boardNum, subject FROM qnaboard b JOIN member1 m ON b.userId=m.userId ");                
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
                dto=new qaBoardDTO();
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

    // ������
    public qaBoardDTO nextReadBoard(int groupNum, int orderNo, String searchKey, String searchValue) {
        qaBoardDTO dto=null;

        PreparedStatement pstmt=null;
        ResultSet rs=null;
        StringBuffer sb = new StringBuffer();

        try {
            if(searchValue!=null && searchValue.length() != 0) {
                sb.append("SELECT ROWNUM, tb.* FROM ( ");
                sb.append("  SELECT boardNum, subject ");
    			sb.append("               FROM qnaboard b");
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
                sb.append("     SELECT boardNum, subject FROM qnaboard b JOIN member1 m ON b.userId=m.userId ");
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
                dto=new qaBoardDTO();
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
    
    
    // �亯�ΰ�� orderNo ����
    public int updateOrderNo(int groupNum, int orderNo) {
    	int result=0;
    	PreparedStatement pstmt=null;
    	String sql;
    	
    	try {
			sql="UPDATE qnaboard SET orderNo=orderNo+1";
			sql+= "WHERE groupNum=? AND orderNo>?";
			pstmt=conn.prepareStatement(sql);
			
			pstmt.setInt(1, groupNum);
			pstmt.setInt(2, orderNo);
			
			result=pstmt.executeUpdate();
			pstmt.close();
			
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	return result;
    }
    
    public int deleteBoard(int boardNum) {
    	int result=0;
    	PreparedStatement pstmt = null;
    	String sql;
    	
    	try {
			sql="DELETE FROM qnaboard WHERE boardNum IN ";
			sql+="(SELECT boardNum FROM qnaboard START WITH boardNum=? CONNECT BY PRIOR boardNum=parent)";
			// ���ϰ� �ؿ� �ڽĵ� ����(������ ������ ���� ����)
			
			pstmt=conn.prepareStatement(sql);
			pstmt.setInt(1, boardNum);
			result = pstmt.executeUpdate();
			pstmt.close();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
    	return result;
    }
	
	
}
