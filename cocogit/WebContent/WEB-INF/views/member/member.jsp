<%@ page contentType="text/html; charset=UTF-8" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>
<%
   String cp = request.getContextPath();// 
%>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta http-equiv="X-UA-Compatible" content="IE=edge">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>우리들의 IT 이야기</title>

<link rel="stylesheet" href="<%=cp%>/res/jquery/css/smoothness/jquery-ui.min.css" type="text/css"/>
<link rel="stylesheet" href="<%=cp%>/res/bootstrap/css/bootstrap.min.css" type="text/css"/>
<link rel="stylesheet" href="<%=cp%>/res/bootstrap/css/bootstrap-theme.min.css" type="text/css"/>

<link rel="stylesheet" href="<%=cp%>/res/css/style.css" type="text/css"/>
<link rel="stylesheet" href="<%=cp%>/res/css/layout/layout.css" type="text/css"/>

<link href="<%=cp%>/bootstrap/css/bootstrap.min.css" rel="stylesheet">
<%-- <script src="<%=cp%>/jquery/jquery-3.1.1.min.js"></script>
<script src="<%=cp%>/bootstrap/js/bootstrap.min.js"></script> --%>

<script type="text/javascript">

	function check()
	{
		var f = document.memberForm;
		var str;
	
		str = f.userId.value;
		
		if(!/^[a-z][a-z0-9_]{4,9}$/i.test(str))
		{ 
			alert("우리 ID 유효성검사 빡시다~ 다시해라~");
			return false;
		}
		
		str = f.userPwd.value;
		
		if(!/^(?=.*[a-z])(?=.*[!@#$%^*+=-]|.*[0-9]).{5,10}$/i.test(str))
		{ 
			alert("유효성검사 빡세게 작업했다 비밀번호 다시 써라 ㅡㅡ");
			return false;
		}
		
	 	if(f.userPwdCheck.value != str)
	 	{
			alert("비밀번호 위에꺼랑 똑같이 입력해라고 ㅡㅡ");
			return false;
		}
		
 	 	str = f.userName.value;
		
	    if(!str)
	    {
	    	alert("이름 똑바로 입력해라 ㅡㅡ");
	        return false;
	    }
		
	    str = f.birth.value;
	    
 	   /*  if(! isValidDateFormat(str))
	    {
	    	alert("형식 맞춰서 입력해 개나리같은 시베리안 허스키야 ㅡㅡ");
	        return false;
	    }  
	
 	    str = f.email.value;
	    
	    if(!isValidEmail)
	    {
	    	alert("이메일업냐? 아재여? ㅇ? ㅇㅇ? ㅡㅡ?");
	        return false;
	    } 
	    
	    alert("1");
	    
 	    str = f.tel1.value;
	    
	    if(!str)
	    {
	    	alert("패스워드가 일치하지 않습니다.");
	        return false;
	    }
	
	    str = f.tel2.value;
	    if(!/^(\d+)$/.test(str)) {
	        f.tel2.focus();
	        return false;
	    }
	    
	    str = f.tel3.value;
	    if(!/^(\d+)$/.test(str)) {
	        f.tel3.focus();
	        return false;
	    }  
	    
	    alert("2"); */
	    
	    var mode="${mode}";
	    
	    if(mode=="created") {
	    	f.action = "<%=cp%>/member/member_ok.do";
	    } else if(mode=="update") {
	    	f.action = "<%=cp%>/member/update_ok.do";
	    }
	    
	    return true;
	}
	
</script>

</head>
<body>

<div>
    <jsp:include page="/WEB-INF/layout/header.jsp"></jsp:include>
</div>

<div class="container" role="main">
  <div class="jumbotron">
    <h1><span class="glyphicon glyphicon-paste"></span> ${mode=="created"?"회원 가입":"회원 정보 수정"}</h1>
    <p>우리들의 IT이야기에 가입하시면 보다 나은 서비스를 그닥... 딱히.. 뭐 굳이..?? 뭐 없습니다ㅋㅋㅋㅋㅋ 헤헿♥</p>
  </div>

  <div class="bodyFrame">
  <form class="form-horizontal" name="memberForm" method="post" onsubmit="return check();">
    <div class="form-group">
        <label class="col-sm-2 control-label" for="userId">아이디</label>
        <div class="col-sm-7">
            <input class="form-control" id="userId" name="userId" type="text" placeholder="아이디"
                       onchange="userIdCheck();"
                       value="${dto.userId}"
                       ${mode=="update" ? "readonly='readonly' style='border:none;'":""}>
            <p class="help-block">아이디는 5~10자 이내이며, 첫글자는 영문자로 시작해야 합니다.</p>
        </div>
    </div>
 
    <div class="form-group">
        <label class="col-sm-2 control-label" for="userPwd">패스워드</label>
        <div class="col-sm-7">
            <input class="form-control" id="userPwd" name="userPwd" type="password" placeholder="비밀번호">
            <p class="help-block">패스워드는 5~10자이며 하나 이상의 숫자나 특수문자가 포함되어야 합니다.</p>
        </div>
    </div>
    
    <div class="form-group">
        <label class="col-sm-2 control-label" for="userPwdCheck">패스워드 확인</label>
        <div class="col-sm-7">
            <input class="form-control" id="userPwdCheck" name="userPwdCheck" type="password" placeholder="비밀번호 확인">
            <p class="help-block">패스워드를 한번 더 입력해주세요.</p>
        </div>
    </div>
 
    <div class="form-group">
        <label class="col-sm-2 control-label" for="userName">이름</label>
        <div class="col-sm-7">
            <input class="form-control" id="userName" name="userName" type="text" placeholder="이름"
                       value="${dto.userName}" ${mode=="update" ? "readonly='readonly' style='border:none;' ":""}>
        </div>
    </div>
 
    <div class="form-group">
        <label class="col-sm-2 control-label" for="birth">생년월일</label>
        <div class="col-sm-7">
            <input class="form-control" id="birth" name="birth" type="text" placeholder="생년월일" value="${dto.birth}">
            <p class="help-block">생년월일은 2000-01-01 형식으로 입력 합니다.</p>
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-2 control-label" for="email">이메일</label>
        <div class="col-sm-7">
            <input class="form-control" id="email" name="email" type="email" placeholder="이메일" value="${dto.email}">
        </div>
    </div>
    
    <div class="form-group">
        <label class="col-sm-2 control-label" for="tel1">전화번호</label>
        <div class="col-sm-7">
             <div class="row">
                  <div class="col-sm-3" style="padding-right: 5px;">
						  <select class="form-control" id="tel1" name="tel1" >
								<option value="">선 택</option>
								<option value="010" ${dto.tel1=="010" ? "selected='selected'" : ""}>010</option>
								<option value="011" ${dto.tel1=="011" ? "selected='selected'" : ""}>011</option>
								<option value="016" ${dto.tel1=="016" ? "selected='selected'" : ""}>016</option>
								<option value="017" ${dto.tel1=="017" ? "selected='selected'" : ""}>017</option>
								<option value="018" ${dto.tel1=="018" ? "selected='selected'" : ""}>018</option>
								<option value="019" ${dto.tel1=="019" ? "selected='selected'" : ""}>019</option>
						  </select>
                  </div>

                  <div class="col-sm-1" style="width: 1%; padding-left: 5px; padding-right: 10px;">
                         <p class="form-control-static">-</p>
                  </div>
                 <div class="col-sm-2" style="padding-left: 5px; padding-right: 5px;">
 						  <input class="form-control" id="tel2" name="tel2" type="text" value="${dto.tel2}" maxlength="4">
                  </div>
                  <div class="col-sm-1" style="width: 1%; padding-left: 5px; padding-right: 10px;">
                         <p class="form-control-static">-</p>
                  </div>
                  <div class="col-sm-2" style="padding-left: 5px; padding-right: 5px;">
						  <input class="form-control" id="tel3" name="tel3" type="text" value="${dto.tel3}" maxlength="4">
                  </div>
             </div>
        </div>
    </div>
    
    <div class="form-group">
        <label class="col-sm-2 control-label" for="zip">우편번호</label>
        <div class="col-sm-7">
             <div class="row">
                  <div class="col-sm-3" style="padding-right: 0px;">
						  <input class="form-control" id="zip" name="zip" type="text" value="${dto.zip}" maxlength="7" readonly="readonly">
                  </div>
                  <div class="col-sm-1" style="width: 1%; padding-left: 5px; padding-right: 5px;">
                         <button type="button" class="btn btn-default">우편번호</button>
                  </div>
             </div>
        </div>
    </div>
    
    <div class="form-group">
        <label class="col-sm-2 control-label" for="addr1">주소</label>
        <div class="col-sm-7">
            <input class="form-control" id="addr1" name="addr1" type="text" placeholder="기본주소" readonly="readonly" value="${dto.addr1}">
            <input class="form-control" id="addr2" name="addr2" type="text" placeholder="나머지주소" value="${dto.addr2}" style="margin-top: 5px;">
        </div>
    </div>

    <div class="form-group">
        <label class="col-sm-2 control-label" for="job">직업</label>
        <div class="col-sm-7">
            <input class="form-control" id="job" name="job" type="text" placeholder="직업" value="${dto.zip}">
        </div>
    </div>
    

<c:if test="${mode=='created'}">
    <div class="form-group">
        <label class="col-sm-2 control-label" for="agree">약관 동의</label>
        <div class="col-sm-7 checkbox">
            <label>
                <input id="agree" name="agree" type="checkbox" checked="checked"
                         onchange="form.sendButton.disabled = !checked"> <a href="#">이용약관</a>에 동의합니다.
            </label>
        </div>
    </div>
</c:if>
     
    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
<c:if test="${mode=='created'}">
            <button type="submit" name="sendButton" class="btn btn-primary">회원가입 <span class="glyphicon glyphicon-ok"></span></button>
            <button type="button" class="btn btn-danger" onclick="javascript:location.href='<%=cp%>/';">가입취소 <span class="glyphicon glyphicon-remove"></span></button>
</c:if>            
<c:if test="${mode=='update'}">
            <button type="submit" class="btn btn-primary">정보수정 <span class="glyphicon glyphicon-ok"></span></button>
            <button type="button" class="btn btn-danger" onclick="javascript:location.href='<%=cp%>/';">수정취소 <span class="glyphicon glyphicon-remove"></span></button>
</c:if>            
        </div>
    </div>

    <div class="form-group">
        <div class="col-sm-offset-2 col-sm-10">
                <p class="form-control-static">${message}</p>
        </div>
    </div>
     
  </form>
  </div>

</div>

<%-- <script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery.ui.datepicker-ko.js"></script>
<script type="text/javascript" src="<%=cp%>/res/bootstrap/js/bootstrap.min.js"></script> --%>
</body>
</html>