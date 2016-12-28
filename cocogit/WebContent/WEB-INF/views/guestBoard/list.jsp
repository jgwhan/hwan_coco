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
<title>study</title>

<style type="text/css">
.guest-write {
    border: #d5d5d5 solid 1px;
    padding: 10px;
    min-height: 50px;
}
</style>

<script type="text/javascript">
function sendGuest() {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		location.href="<%=cp%>/member/login.do";
		return;
	}
	
	var f=document.guestForm;
	var str;
	
	str=f.content.value;
	if(!str) {
		alert("내용을 입력 하세요 !!!");
		f.content.focus();
		return;
	}
	
	f.action="<%=cp%>/guestBoard/guest_ok.do";
	f.submit();
}

function deleteGuest(num, uid) {
	var url="<%=cp%>/guestBoard/delete.do?num="+num+"&page=${page}&uid="+uid;
	
	if(confirm("삭제 하시겠습니까 ?"))
		location.href=url;
}
</script>
</head>
<link href="<%=cp%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<script src="<%=cp%>/jquery/jquery-3.1.1.min.js"></script>
<script src="<%=cp%>/bootstrap/js/bootstrap.min.js"></script>

<body>
	<div>
    	<jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
	</div>

	<div class="layoutBody">

		<div style="min-height: 450px;">
			<div
				style="width: 100%; height: 40px; line-height: 40px; clear: both; border-top: 1px solid #DAD9FF; border-bottom: 1px solid #DAD9FF;">
				<div
					style="width: 600px; height: 30px; line-height: 30px; margin: 5px auto;">
					<%-- <img src="<%=cp%>/res/images/arrow.gif" alt="" style="padding-left: 5px; padding-right: 5px;"> --%>
					<span class="glyphicon glyphicon-list-alt"></span>방명록
				</div>
			</div>

			<div
				style="margin: 10px auto; margin-top: 30px; width: 600px; min-height: 400px;">
				<form name="guestForm" method="post" action="">
					<div class="guest-write">
						<div style="clear: both;">
							<span style="font-weight: bold;">방명록쓰기</span><span> -
								방명록이셈~ 암거나 쓰셈~ 헤헿</span>
						</div>
						<div style="clear: both; padding-top: 10px;">
							<textarea name="content" id="content" class="boxTF" rows="3"
								style="display: block; width: 100%; padding: 6px 12px; box-sizing: border-box;"
								required="required"></textarea>
						</div>
						<div style="text-align: right; padding-top: 10px;">
							<button type="button" class="btn" onclick="sendGuest();"
								style="padding: 8px 25px;">등록하기</button>
						</div>
					</div>
				</form>

				<div id="listGuest" style="width: 600px; margin: 0px auto;">

					<c:if test="${dataCount != 0}">
						<table
							style='width: 600px; margin: 10px auto 0px; border-spacing: 0px; border-collapse: collapse;'>
							<c:forEach var="dto" items="${list}">
								<tr height='30' bgcolor='#EEEEEE'
									style='border: 1px solid #DBDBDB;'>
									<td width='50%' style='padding-left: 5px;'>작성자 |
										${dto.userName }</td>
									<td width='50%' align='right' style='padding-right: 5px;'>
										${dto.created} <c:if
											test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">	
									              | <a
												href="javascript:deleteGuest('${dto.num}', '${dto.userId}');">삭제</a>
										</c:if>
									</td>
								</tr>

								<tr height='50'>
									<td colspan='2' style='padding: 5px;' valign='top'>${dto.content}</td>
								</tr>
							</c:forEach>

							<tr>
								<td colspan='2' height='30' align='center'>${paging}</td>
							</tr>
						</table>
					</c:if>
				</div>

			</div>

		</div>

	</div>

	<div>
		<jsp:include page="/WEB-INF/layout/footer.jsp"></jsp:include>
	</div>

</body>
</html>