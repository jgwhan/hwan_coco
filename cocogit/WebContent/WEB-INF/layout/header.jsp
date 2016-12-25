<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%
   request.setCharacterEncoding("UTF-8");
	String cp = request.getContextPath();
%>
	<h2 style="margin-left: 30px;"><span class="glyphicon glyphicon-heart"></span> 쭈니장군의 일망타진 IT 블로그 22</h2>

<nav class="navbar navbar-inverse" style="margin-left: 30px; margin-right: 30px">
  <div class="container-fluid">
    <div class="navbar-header">
      <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#bs-example-navbar-collapse-2">
        <span class="sr-only">Toggle navigation</span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
        <span class="icon-bar"></span>
      </button>
      <a class="navbar-brand" href="#">소개</a>
    </div>

    <div class="collapse navbar-collapse" id="bs-example-navbar-collapse-2">
      <ul class="nav navbar-nav">
        <li class="active"><a href="<%=cp%>/freeBoard/list.do">자유게시판 <span class="sr-only">(current)</span></a></li>
        <li><a href="<%=cp%>/freePhoto/list.do">자유갤러리</a></li>
        <li class="dropdown">
          <a href="#" class="dropdown-toggle" data-toggle="dropdown" role="button" aria-expanded="false" aria-haspopup="true">공부게시판 <span class="caret"></span></a>
          <ul class="dropdown-menu" role="menu">
            <li><a href="<%=cp%>/javaBoard/list.do">자바게시판</a></li>
            <li><a href="<%=cp%>/webBoard/list.do">웹게시판</a></li>
            <li><a href="<%=cp%>/studyBoard/list.do">스터디모집</a></li>
          </ul>
        </li>
         <li><a href="<%=cp%>/qaBoard/list.do">질문게시판</a></li>
        <li><a href="<%=cp%>/guestBoard/list.do">방명록</a></li>
      </ul>
      <form class="navbar-form navbar-right" role="search">
        <div class="form-group">
          <input type="text" class="form-control" placeholder="Search">
        </div>
        <button type="submit" class="btn btn-default">검색</button>
      </form>
<!--       <ul class="nav navbar-nav navbar-right">
        <li><a href="#">Link</a></li>
      </ul> -->
    </div>
  </div>
</nav>  