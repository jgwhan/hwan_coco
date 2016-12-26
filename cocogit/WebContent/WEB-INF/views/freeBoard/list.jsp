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
</head>

<div>
    <jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
</div>

<body>
 자유게시판 목록 폼 입니다.
</body>
</html>