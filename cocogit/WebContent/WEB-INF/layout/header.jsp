<%@page import="com.util.CountManager"%>
<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
   request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>

<style type="text/css">
a:link {
	color: black;
	text-decoration: none;
}

a:visited {
	color: black;
	text-decoration: none;
}

a:hover {
	color: black;
	text-decoration: none;
}
</style>
<div class="container">
	<div class="main-a">
		<h2 style="margin-left: 30px;">
			<a href="<%=cp%>/"><span class="glyphicon glyphicon-heart"></span>
				객체지향 IT 블로그 </a>
		</h2>
	</div>

	<div class="login header-login" align="right"
		style="margin-right: 30px;">
		<c:if test="${empty sessionScope.member}">
			<a href="<%=cp%>/member/login.do"><span
				class="glyphicon glyphicon-log-in">로그인</span> </a>
			<i></i>
			<a href="<%=cp%>/member/member.do"><span
				class="glyphicon glyphicon-user" style="margin-left: 10px"></span>
				회원가입</a>
		</c:if>
		<c:if test="${not empty sessionScope.member}">
			<span style="color: blue;">${sessionScope.member.userName}</span>님 <i></i>
			<c:if test="${sessionScope.member.userId=='admin'}">
				<a href="<%=cp%>/admin/main.do">관리자</a>
				<i></i>
			</c:if>
			<a href="<%=cp%>/member/logout.do"><span
				class="glyphicon glyphicon-log-out"></span> 로그아웃</a>
		</c:if>
	</div>


	<nav class="navbar navbar-inverse"
		style="margin-left: 30px; margin-right: 30px">
		<div class="container-fluid">
			<div class="navbar-header">
				<button type="button" class="navbar-toggle collapsed"
					data-toggle="collapse" data-target="#bs-example-navbar-collapse-2">
					<span class="sr-only">Toggle navigation</span> <span
						class="icon-bar"></span> <span class="icon-bar"></span> <span
						class="icon-bar"></span>
				</button>
				<a class="navbar-brand" href="#">소개</a>
			</div>

			<div class="collapse navbar-collapse"
				id="bs-example-navbar-collapse-2">
				<ul class="nav navbar-nav">
					<li class="active"><a href="<%=cp%>/freeBoard/list.do">자유게시판
							<span class="sr-only">(current)</span>
					</a></li>
					<li><a href="<%=cp%>/freePhoto/list.do">엄빠주의!!</a></li>
					<li class="dropdown"><a href="#" class="dropdown-toggle"
						data-toggle="dropdown" role="button" aria-expanded="false"
						aria-haspopup="true">공부게시판 <span class="caret"></span></a>
						<ul class="dropdown-menu" role="menu">
							<li><a href="<%=cp%>/javaBoard/list.do">자바게시판</a></li>
							<li><a href="<%=cp%>/webBoard/list.do">웹게시판</a></li>
							<li><a href="<%=cp%>/studyBoard/list.do">스터디모집</a></li>
						</ul></li>
					<li><a href="<%=cp%>/qaBoard/list.do">질문게시판</a></li>
					<li><a href="<%=cp%>/guestBoard/list.do">방명록</a></li>
				</ul>
				<form class="navbar-form navbar-right" role="search">
					<div class="form-group">
						<input type="text" class="form-control" placeholder="Search">
					</div>
					<button type="submit" class="btn btn-default">검색</button>
				</form>

			</div>
		</div>
	</nav>
	
	<div class="count header" align="right"
		style="margin-right: 30px;">
		<span>
         	오늘 방문자수 : <%=CountManager.getToDayCount() %>&nbsp;
         	전체 방문자수 : <%=CountManager.getTotalCount() %><br>
		</span>
	</div>
	
</div>