<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>spring</title>
<link href="<%=cp%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="<%=cp%>/jquery/jquery-3.1.1.min.js"></script>
<script src="<%=cp%>/bootstrap/js/bootstrap.min.js"></script>


<style type="text/css">
.bbs-article .table { 
    margin-top: 15px;
}
.bbs-article .table thead tr, .bbs-article .table tbody tr {
    height: 30px;
}
.bbs-article .table thead tr th, .bbs-article .table tbody tr td {
    font-weight: normal;
    border-top: none;
    border-bottom: none;
}
.bbs-article .table thead tr {
    border-top: #d5d5d5 solid 1px;
    border-bottom: #dfdfdf solid 1px;
} 
.bbs-article .table tbody tr {
    border-bottom: #dfdfdf solid 1px;
}
.bbs-article .table i {
    background: #424951;
    display: inline-block;
    margin: 0 7px 0 7px;
    position: relative;
    top: 2px;
    width: 1px;
    height: 13px;    
}
</style>

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-1.12.4.min.js"></script>

<script type="text/javascript">
<c:if test="${sessionScope.member.userId=='admin' || sessionScope.member.userId== dto.userId}">
function deleteBoard(num) {
	if(confirm("게시물을 삭제 하시겠습니까 ? ")) {
		var url="<%=cp%>/qaBoard/delete.do?boardNum="+num+"&page=${page}&rows=${rows}";
		location.href=url;
	}
}
</c:if>

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
	          <h3><span class="glyphicon glyphicon-book"></span> 자게이다. </h3>
	    </div>
	    
	     <div class="alert alert-default" role="alert">
	        <i class="glyphicon-question-sign "></i>  자게
	    </div>
	    
	    <div class="table-responsive" style="clear: both;">
	        <div class="bbs-article">
	            <table class="table">
	                 <thead>
	                     <tr>
	                         <th colspan="2" style="text-align: center;">
	                                 ${dto.subject}
	                         </th>
	                     </tr>
	                <thead>
	                 <tbody>
	                     <tr>
	                         <td style="text-align: left;">
	                             이름 : ${dto.userName}
	                         </td>
	                         <td style="text-align: right;">
	                          ${dto.created} <i></i>조회 : ${dto.hitCount}
	                         </td>
	                     </tr>
	                     <tr>
	                         <td colspan="2" style="height: 230px;">
	                              ${dto.content}
	                         </td>
	                     </tr>
	                     <tr>
	                         <td colspan="2">
	                              <span style="display: inline-block; min-width: 45px;">이전글</span> :
	                              <c:if test="${not empty preReadDto }">
								      <a href="<%=cp%>/qaBoard/article.do?${params}&boardNum=${preReadDto.boardNum}">${preReadDto.subject}</a>
							       </c:if>
	                         </td>
	                     </tr>
	                     <tr>
	                         <td colspan="2" style="border-bottom: #d5d5d5 solid 1px;">
	                              <span style="display: inline-block; min-width: 45px;">다음글</span> :
	                              <c:if test="${not empty nextReadDto }">
								      <a href="<%=cp%>/qaBoard/article.do?${params}&boardNum=${nextReadDto.boardNum}">${nextReadDto.subject}</a>
							       </c:if>
	                         </td>
	                     </tr>                                          
	                </tbody>
	                <tfoot>
	                	<tr>
	                		<td>
	                		    <button type="button" class="btn btn-default btn-sm wbtn" onclick="javascript:location.href='<%=cp%>/qaBoard/reply.do?boardNum=${dto.boardNum}&page=${page}&rows=${rows}';">답변</button>
	                		    <c:if test="${sessionScope.member.userId== dto.userId}">
	                		        <button type="button" class="btn btn-default btn-sm wbtn" onclick="javascript:location.href='<%=cp%>/qaBoard/update.do?boardNum=${dto.boardNum}&page=${page}&rows=${rows}'">수정</button>
	                		    </c:if>
	                		    <c:if test="${sessionScope.member.userId=='admin' || sessionScope.member.userId== dto.userId}">
	                		         <button type="button" class="btn btn-default btn-sm wbtn" onclick="deleteBoard('${dto.boardNum}');">삭제</button>
	                		     </c:if> 
	                		</td>
	                		<td align="right">
	                		    <button type="button" class="btn btn-default btn-sm wbtn" onclick="javascript:location.href='<%=cp%>/qaBoard/list.do?${params}';"> 목록으로 </button>
	                		</td>
	                	</tr>
	                </tfoot>
	            </table>
	       </div>
	   </div>

    </div>
</div>



<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery.ui.datepicker-ko.js"></script>
<script type="text/javascript" src="<%=cp%>/res/bootstrap/js/bootstrap.min.js"></script>
</div>
</body>
</html>