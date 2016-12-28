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
<title>우리들의 IT 이야기</title>

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
.bbs-reply {
    font-family: NanumGothic, 나눔고딕, "Malgun Gothic", "맑은 고딕", 돋움, sans-serif;
}

.bbs-reply-write {
    border: #d5d5d5 solid 1px;
    padding: 10px;
    min-height: 50px;
}
</style>

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript">
function updatePhoto(num) {
	<c:if test="${sessionScope.member.userId==dto.userId}">
	     var url="<%=cp%>/freePhoto/update.do?num="+num+"&page=${page}";
	     location.href=url;
	</c:if>
}

function deletePhoto(num) {
	<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
         if(confirm("게시물을 삭제 하시겠습니까 ?")) {
        	 var url="<%=cp%>/freePhoto/delete.do?num="+num+"&page=${page}";
        	 location.href=url;
         }	
	</c:if>
}
//댓글 리스트
$(function(){
	listPage(1);
});

function listPage(page) {
	var url="<%=cp%>/freePhoto/listReply.do";
	var num="${dto.num}";
	$.post(url, {num:num, pageNo:page}, function(data){
		$("#listReply").html(data);
	});
}

//리플 저장
function sendReply() {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return false;
	}

	var num="${dto.num}"; // 해당 게시물 번호
	var content=$.trim($("#content").val());
	if(! content ) {
		alert("내용을 입력하세요 !!! ");
		$("#content").focus();
		return false;
	}
	
	var params="num="+num;
	params+="&content="+content;
	
	$.ajax({
		type:"POST"
		,url:"<%=cp%>/freePhoto/insertReply.do"
		,data:params
		,dataType:"json"
		,success:function(data) {
			$("#content").val("");
			
  			var state=data.state;
			if(state=="true") {
				listPage(1);
			} else if(state=="false") {
				alert("댓글을 등록하지 못했습니다. !!!");
			} else if(state=="loginFail") {
				login();
			}
		} 
		,error:function(e) {
			alert(e.responseText);
		}
	});
}

// 리플 삭제
function deleteReply(replyNum, pageNo, userId) {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return false;
	}
	
	if(confirm("게시물을 삭제하시겠습니까 ? ")) {	
		var url="<%=cp%>/freePhoto/deleteReply.do";
		$.post(url, {replyNum:replyNum, userId:userId}, function(data){
		        var state=data.state;
				if(state=="loginFail") {
					login();
				} else {
					listPage(pageNo);
				}
		}, "json");
	}
}
</script>
</head>
<body style="background: #f37070"><!-- style="background: #f37070" -->

<div>
    <jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
</div>

<div class="container" role="main">
    <div class="bodyFrame col-sm-11"  style="float:none; margin-left: auto; margin-right: auto;">

	    <div class="body-title" style="color: white; border: 5px; border-color: white; ">
	          <h3><span class="glyphicon glyphicon-picture"></span> 성인 갤러리 </h3>
	    </div>
	    
	    <div class="alert alert-info" style="background: red; color: black">
	        <i class="glyphicon glyphicon-info-sign"></i> 어떰? 괜춘??
	    </div>
	    
	    <div class="table-responsive" style="clear: both;">
	        <div class="bbs-article">
	            <table class="table">
	                 <thead>
	                  <tr>
	                         <th  style="text-align: left;">
	                             	제목 :  ${dto.subject}
	                         </th>
	                         <td style="text-align: right;">
	                          조회수 : ${dto.hitCount}
	                         </td>
	                     </tr>
	                    
	                    
	                <thead>
	                 <tbody>
	                     <tr>
	                         <td style="text-align: left;">
	                             	이름 : ${dto.userName}
	                         </td>
	                         <td style="text-align: right;">
	                          ${dto.created}
	                         </td>
	                     </tr>
                         <tr style="border-bottom:none;">
                             <td colspan="2">
                                 <img src="<%=cp%>/uploads/freePhoto/${dto.imageFilename}" style="max-width:100%; height:auto; resize:both;">
                             </td>
                         </tr>
	                     <tr>
	                         <td colspan="2" style="min-height: 30px;">
	                              ${dto.content}
	                         </td>
	                     </tr>
	                </tbody>
	                <tfoot>
							<tr>
	                		<td>
		                        <c:if test="${sessionScope.member.userId==dto.userId}">		                		
	                		        <button type="button" class="btn btn-default btn-sm wbtn" onclick="updatePhoto(${dto.num});">수정</button>
	                		    </c:if>
		                        <c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">    
	                		        <button type="button" class="btn btn-default btn-sm wbtn" onclick="deletePhoto(${dto.num});">삭제</button>
	                		    </c:if>
	                		</td>
	                		
	                		<td align="right">
	                		    <button type="button" class="btn btn-default btn-sm wbtn"
	                		                onclick="javascript:location.href='<%=cp%>/freePhoto/list.do?page=${page}';"> 목록으로 </button>
	                		</td>
	                	</tr>
	                	
	                </tfoot>
	            </table>
	            <div class="bbs-reply">
								<div class="bbs-reply-write">
									<div style="clear: both;">
										<div style="float: left;">
											<span style="font-weight: bold;">댓글쓰기</span><span> -
												댓글쓰기가 될 거 같지만 안됨</span>
										</div>
										<div style="float: right; text-align: right;"></div>
									</div>
									<div style="clear: both; padding-top: 10px;">
										<textarea id="content" class="form-control" rows="3"
											required="required"></textarea>
									</div>
									<div style="text-align: right; padding-top: 10px;">
										<button type="button" class="btn btn-primary btn-sm"
											onclick="sendReply();"
											style="background-color: black; border-color: black">
											댓글등록 <span class="glyphicon glyphicon-ok"></span>
										</button>
									</div>
								</div>

								<div id="listReply"></div>
							</div>
	       </div>
	   </div>

    </div>
</div>

<div>
    <jsp:include page="/WEB-INF/layout/footer.jsp"></jsp:include>
</div>

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery.ui.datepicker-ko.js"></script>
<script type="text/javascript" src="<%=cp%>/res/bootstrap/js/bootstrap.min.js"></script>
</body>
</html>