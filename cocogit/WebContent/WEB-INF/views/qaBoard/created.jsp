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
.bs-write table {
    width: 100%;
    border: 0;
    border-spacing: 0;
}
.table tbody tr td {
    border-top: none;
    font-weight: normal;
	font-family: NanumGothic, 나눔고딕, "Malgun Gothic", "맑은 고딕", 돋움, sans-serif;
} 
.bs-write table td {
	padding: 3px 3px 3px 3px;
}

.bs-write .td1 {
    min-width: 100px;
    min-height: 30px;
    color: #666;
    vertical-align: middle;
}

.bs-write .td2 {
}

.bs-write .td3 {
}

.bs-write .td4 {
}
</style>

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">
  function check() {
        var f = document.boardForm;

    	var str = f.subject.value;
        if(!str) {
            f.subject.focus();
            return false;
        }

    	str = f.content.value;
        if(!str) {
            f.content.focus();
            return false;
        }

        var mode="${mode}";
    	if(mode=="created")
    		f.action="<%=cp%>/qaBoard/created_ok.do";
    	else if(mode=="update")
    		f.action="<%=cp%>/qaBoard/update_ok.do";
       	else if(mode=="reply")
       		f.action="<%=cp%>/qaBoard/reply_ok.do";

    	// <input type='submit' ..>,  <input type='image' ..>, <button>은 submit() 메소드 호출하면 두번전송
        return true;
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
	    
	    <div class="alert">
	        <i class="glyphicon-question-sign "></i>  질문과 답변 게시판입니다. 이곳에 질문을 올리시면 친절과 정성을 담아 답변해드립니다.
	    </div>
	    
	    <div>
	        <form name="boardForm" method="post" onsubmit="return check();">
	            <div class="bs-write">
	                <table class="table">
	                    <tbody>
	                        <tr>
	                            <td class="td1">작성자명</td>
	                            <td class="td2 col-md-5 col-sm-5">
	                                <p class="form-control-static">${sessionScope.member.userName}</p>
	                            </td>
	                            <td class="td1" align="center">&nbsp;</td>
	                            <td class="td2 col-md-5 col-sm-5">
	                                &nbsp;
	                            </td>
	                        </tr>
	                        <tr>
	                            <td class="td1">제목</td>
	                            <td colspan="3" class="td3">
	                                <input type="text" name="subject" class="form-control input-sm" value="${dto.subject}" required="required">
	                            </td>
	                        </tr>
	                        <tr>
	                            <td class="td1" colspan="4" style="padding-bottom: 0px;">내용</td>
	                        </tr>
	                        <tr>
	                            <td colspan="4" class="td4">
	                            	<textarea name="content" class="form-control" rows="13" required="required">${dto.content}</textarea>
	                            </td>
	                        </tr>
	                    </tbody>
	                    <tfoot>
	                        <tr>
	                            <td colspan="4" style="text-align: center; padding-top: 15px;">
	                                  <c:if test="${mode=='update'}">
	                                          <input type="hidden" name="boardNum" value="${dto.boardNum}">
	                                          <input type="hidden" name="page" value="${page}">
	                                          <input type="hidden" name="rows" value="${rows}">
	                                  </c:if>
	                                  <c:if test="${mode=='reply'}">
	                                          <input type="hidden" name="groupNum" value="${dto.groupNum}">
	                                          <input type="hidden" name="depth" value="${dto.depth}">
	                                          <input type="hidden" name="orderNo" value="${dto.orderNo}">
	                                          <input type="hidden" name="parent" value="${dto.boardNum}">
	                                          <input type="hidden" name="page" value="${page}">
	                                          <input type="hidden" name="rows" value="${rows}">
	                                  </c:if>
	                                  <button type="submit" class="btn btn-default"> 확인 <span class="glyphicon glyphicon-ok"></span></button>
	                                  <button type="button" class="btn btn-default" onclick="javascript:location.href='<%=cp%>/qaBoard/list.do';"> 취소 </button>
	                            </td>
	                        </tr>
	                    </tfoot>
	                </table>
	            </div>
	        </form>
	    </div>
    
    </div>
</div>



<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery.ui.datepicker-ko.js"></script>
<script type="text/javascript" src="<%=cp%>/res/bootstrap/js/bootstrap.min.js"></script>
</div>
<div>
    <jsp:include page="/WEB-INF/layout/footer.jsp"></jsp:include>
</div>
</body>
</html>