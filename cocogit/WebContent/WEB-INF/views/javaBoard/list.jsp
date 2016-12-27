<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
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
<link href="<%=cp%>/bootstrap/css/bootstrap.css" rel="stylesheet">

</head>
<body>
	<div>
		<jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
	</div>

	<div class="container" role="main">
		<div class="bodyFrame col-sm-10"
			style="float: none; margin-left: auto; margin-right: auto;">

			<div class="body-title">
				<h3>
					<span class="glyphicon glyphicon-education"></span> 자바 게시판 
				</h3>
			</div>

			<div class="alert alert-info" style="background-color: #D8D8D8; 
			border-color: activeborder; color: black">
				<i class="glyphicon glyphicon-leaf"></i> 자바에 대해 알아봅시다!
			</div>

			<div>
				<div style="clear: both; height: 35px; line-height: 35px;">
					<div style="float: left;">${dataCount}개(${page}/${total_page}
						페이지)</div>
					<div style="float: right;">
						<form name="selectListForm" action="<%=cp%>/javaBoard/list.do"
							method="post">
							<select class="form-control input-sm" name="rows"
								onchange="selectList()">
								<option value="5" ${rows==5?"selected='selected'":""}>5개씩
									출력</option>
								<option value="10" ${rows==10?"selected='selected'":""}>10개씩
									출력</option>
								<option value="20" ${rows==20?"selected='selected'":""}>20개씩
									출력</option>
								<option value="30" ${rows==30?"selected='selected'":""}>30개씩
									출력</option>
							</select> <input type="hidden" name="searchKey" value="${searchKey}">
							<input type="hidden" name="searchValue" value="${searchValue}">
						</form>
					</div>
				</div>

				<div class="table-responsive" style="clear: both;">
					<!-- 테이블 반응형 -->
					<table class="table table-striped">
						<thead style="background-color: black; color: white">
							<tr>
								<th class="text-center" style="width: 70px;">번호</th>
								<th>제목</th>
								<th class="text-center" style="width: 100px;">글쓴이</th>
								<th class="text-center" style="width: 100px;">날짜</th>
								<th class="text-center" style="width: 70px;">조회수</th>
							</tr>
						</thead>
						<tbody>

							<c:forEach var="dto" items="${list}">
								<tr>
									<td class="text-center">${dto.listNum}</td>
									<td><c:if test="${dto.depth>0}">
											<c:forEach var="i" begin="1" end="${dto.depth}">
		                        			&nbsp;&nbsp;
		                        		</c:forEach>
											<img src="<%=cp%>/images/re.gif">
										</c:if> <a href="${articleUrl}&boardNum=${dto.boardNum}">${dto.subject}</a>
									</td>
									<td class="text-center">${dto.userName}</td>
									<td class="text-center">${dto.created}</td>
									<td class="text-center">${dto.hitCount}</td>
								</tr>
							</c:forEach>

						</tbody>
					</table>
				</div>

				<div class="paging"
					style="text-align: center; min-height: 50px; line-height: 50px;">
					<c:if test="${dataCount==0}">
	           		등록된 게시물이 없습니다.
	           </c:if>
					<c:if test="${dataCount!=0}">
	           		${paging}
	           </c:if>
				</div>

				<div style="clear: both;">
					<div style="float: left; width: 20%; min-width: 85px;">
						<button type="button" class="btn btn-default btn-sm wbtn"
							onclick="javascript:location.href='<%=cp%>/javaBoard/list.do';">새로고침</button>
					</div>
					<div style="float: left; width: 60%; text-align: center;">
						<form name="searchForm" method="post" class="form-inline">
							<select class="form-control input-sm" name="searchKey">
								<option value="subject">제목</option>
								<option value="userName">작성자</option>
								<option value="content">내용</option>
								<option value="created">등록일</option>
							</select> <input type="text" class="form-control input-sm input-search"
								name="searchValue"> <input type="hidden" name="rows"
								value="${rows}">
							<button type="button" class="btn btn-info btn-sm btn-search"
								onclick="searchList();" style="background-color: gray; border-color: gray">
								<span class="glyphicon glyphicon-search"></span> 검색
							</button>
						</form>
					</div>
					<div
						style="float: left; width: 20%; min-width: 85px; text-align: right;">
						<button type="button" class="btn btn-primary btn-sm bbtn"
							onclick="javascript:location.href='<%=cp%>/javaBoard/created.do';" 
							style="background-color: black; border-color: black">
							<span class="glyphicon glyphicon glyphicon-pencil"></span> 글쓰기
						</button>
					</div>
				</div>
			</div>

		</div>
	</div>


</body>
</html>