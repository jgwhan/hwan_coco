<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
   request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link href="<%=cp%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="<%=cp%>/jquery/jquery-3.1.1.min.js"></script>
<script src="<%=cp%>/bootstrap/js/bootstrap.min.js"></script>
<style type="text/css">
.table th, .table td {
    font-weight: normal;
    border-top: none;
}
.table thead tr th{
     border-bottom: none;
} 
.table thead tr{
    border: #d5d5d5 solid 1px;
     background: #eeeeee; color: #787878;
} 
.table td {
    border-bottom: #d5d5d5 solid 1px;
}
.table td a{
    color: #000;
}
</style>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">
function searchList() {
		var f=document.searchForm;
		f.action="<%=cp%>/board/list.do";
		f.submit();
}

function selectList() {
	var f=document.selectListForm;
	f.submit();
}
</script>
</head>
<body>

<div class = container>
	<div>
    <jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
	</div>
<div class="container" role="main">
    <div class="bodyFrame col-sm-11"  style="float:none; margin-left: auto; margin-right: auto;">
        
	    <div class="body-title">
	          <h3><span class="glyphicon glyphicon-book"></span> 질문과 답변 </h3>
	    </div>
	    
	    <div class="alert alert-default" role="alert">
	        <i class="glyphicon-question-sign "></i>  질문과 답변 게시판입니다. 이곳에 질문을 올리시면 친절과 정성을 담아 답변해드립니다.
	    </div>
	
	    <div>
	        <div style="clear: both; height: 35px; line-height: 35px;">
	            <div style="float: left;">${dataCount}개(${page}/${total_page}페이지)</div>
	            <div style="float: right;">
	            	<form name="selectListForm" action="<%=cp%>/board/list.do" method="post">
	            		<select class="form-control input-sm" name="rows" onchange="selectList();">
	            			<option value="5" ${rows==5?"selected='selected'":""}>5개씩 보기</option>
	            			<option value="10"${rows==10?"selected='selected'":""}>10개씩 보기</option>
	            			<option value="20"${rows==20?"selected='selected'":""}>20개씩 보기</option>
	            			<option value="30"${rows==30?"selected='selected'":""}>30개씩 보기</option>
	            		</select>
	            		<input type="hidden" name="searchKey" value="${searchKey}">
	            		<input type="hidden" name="searchValue" value="${searchValue}">
	            	</form>
	            </div>
	        </div>
	        
	        <div class="table-responsive" style="clear: both;"> <!-- 테이블 반응형 -->
	            <table class="table table-hover">
	                <thead>
	                    <tr>
	                        <th class="text-center" style="width: 70px;">번호</th>
	                        <th >제목</th>
	                        <th class="text-center" style="width: 100px;">글쓴이</th>
	                        <th class="text-center" style="width: 100px;">날짜</th>
	                        <th class="text-center" style="width: 70px;">조회수</th> 
	                    </tr>
	                </thead>
	                <tbody>
	                  <c:forEach var="dto" items="${list}">
	                    <tr>
	                        <td class="text-center">${dto.listNum}</td>
	                        <td>
	                        	<c:if test="${dto.depth>0}">
	                        		<c:forEach var="i" begin="1" end="${dto.depth}">
	                        			&nbsp;&nbsp;
	                        		</c:forEach>
	                        		<img src="<%=cp%>/images/re.gif">
	                        	</c:if>
	                        	<a href="${articleUrl}&boardNum=${dto.boardNum}">${dto.subject}</a>
	                        </td>
	                        <td class="text-center">${dto.userName}</td>
	                        <td class="text-center">${dto.created}</td>
	                        <td class="text-center">${dto.hitCount}</td> 
	                    </tr>
	                  </c:forEach>
	                </tbody>
	            </table>
	        </div>
	
	        <div class="paging" style="text-align: center; min-height: 50px; line-height: 50px;">
	          <c:if test="${dataCount==0}">
	        	  등록된 게시물이 없습니다.
	          </c:if>
	          <c:if test="${dataCount!=0}">
	        	  ${paging}
	          </c:if>
	        </div>        
	        
	        <div style="clear: both;">
	        		<div style="float: left; width: 20%; min-width: 85px;">
	        		    <button type="button" class="btn btn-default btn-sm wbtn" onclick="javascript:location.href='<%=cp%>/qaBoard/list.do';">새로고침</button>
	        		</div>
	        		<div style="float: left; width: 60%; text-align: center;">
	        		     <form name="searchForm" method="post" class="form-inline">
							  <select class="form-control input-sm" name="searchKey" >
							      <option value="subject">제목</option>
							      <option value="userName">작성자</option>
							      <option value="content">내용</option>
							      <option value="created">등록일</option>
							  </select>
							  <input type="text" class="form-control input-sm input-search" name="searchValue">
							  <input type="hidden" name="rows" value="${rows}">
							  <button type="button" class="btn btn-default btn-sm btn-search" onclick="searchList();"><span class="glyphicon glyphicon-search"></span> 검색</button>
	        		     </form>
	        		</div>
	        		<div style="float: left; width: 20%; min-width: 85px; text-align: right;">
	        		    <button type="button" class="btn btn-sm bbtn" onclick="javascript:location.href='<%=cp%>/qaBoard/created.do';"><span class="glyphicon glyphicon glyphicon-pencil"></span> 글쓰기</button>
	        		</div>
	        </div>
	    </div>

    </div>
</div>
</div>

</body>
</html>