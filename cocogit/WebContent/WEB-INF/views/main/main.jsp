<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
   request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="utf-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<!-- 위 3개의 메타 태그는 *반드시* head 태그의 처음에 와야합니다; 어떤 다른 콘텐츠들은 반드시 이 태그들 *다음에* 와야 합니다 -->
<title>부트스트랩 101 템플릿</title>

<!-- 부트스트랩 -->
<link href="<%=cp%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<!-- jQuery (부트스트랩의 자바스크립트 플러그인을 위해 필요합니다) -->
<script
	src="<%=cp%>/jquery/jquery-3.1.1.min.js"></script>
<!-- 모든 컴파일된 플러그인을 포함합니다 (아래), 원하지 않는다면 필요한 각각의 파일을 포함하세요 -->
<script src="<%=cp%>/bootstrap/js/bootstrap.min.js"></script>
</head>

<div>
    <jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
</div>

<body >
<div class="jumbotron" style="margin-left: 30px; margin-right: 30px">
  <h1 style="margin-left: 15px">쭈니장군과  함께 IT 공부를!</h1>
  <p style="margin-left: 15px">재식이 기현이 재환이 대호 준휘와 함께해요 뀨</p>
  <img alt="" src="images/gengi.gif" style="margin-left: 15px">
</div>


</body>
</html>