<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>
<script type="text/javascript">
//엔터 처리
$(function(){
	   $("input").not($(":button")).keypress(function (evt) {
	        if (evt.keyCode == 13) {
	            var fields = $(this).parents('form:eq(0),body').find('button,input,textarea,select');
	            var index = fields.index(this);
	            if ( index > -1 && ( index + 1 ) < fields.length ) {
	                fields.eq( index + 1 ).focus();
	            }
	            return false;
	        }
	     });
});
</script>

<nav class="navbar navbar-inverse navbar-fixed-top">
<div class="container">
    <div class="navbar-header">
        <button type="button" class="navbar-toggle collapsed" data-toggle="collapse" data-target="#navbar" aria-expanded="false" aria-controls="navbar">
            <span class="sr-only">Toggle navigation</span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
        </button>
        <a class="navbar-brand" href="<%=cp%>/">SPRING</a>
    </div>
    <div id="navbar" class="navbar-collapse collapse">
          <ul class="nav navbar-nav">
              <li class="active"><a href="<%=cp%>/admin/main.do">Home</a></li>
              <li><a href="#">회원관리</a></li>
              <li><a href="#">커뮤니티관리</a></li>
              <li><a href="#">스터디룸관리</a></li>
              <li><a href="#">고객센터관리</a></li>
          </ul>
          <ul class="nav navbar-nav navbar-right">
              <li><a href="<%=cp%>/member/logout.do">로그아웃</a></li>
           </ul>
          <p class="navbar-text navbar-right"><span>${sessionScope.member.userName}</span> 님</p>
        </div>
</div>
</nav>
