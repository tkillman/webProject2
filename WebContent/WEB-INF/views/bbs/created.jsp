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

<link rel="stylesheet" href="<%=cp%>/res/jquery/css/smoothness/jquery-ui.min.css" type="text/css"/>
<link rel="stylesheet" href="<%=cp%>/res/css/style.css" type="text/css">
<link rel="stylesheet" href="<%=cp%>/res/css/layout/layout.css" type="text/css">

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-1.12.4.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/js/util.js"></script>
<script type="text/javascript">
    function sendBoard() {
        var f = document.boardForm;

    	var str = f.subject.value;
        if(!str) {
            alert("제목을 입력하세요. ");
            f.subject.focus();
            return;
        }

    	str = f.content.value;
        if(!str) {
            alert("내용을 입력하세요. ");
            f.content.focus();
            return;
        }

    	var mode="${mode}";
    	if(mode=="created")
    		f.action="<%=cp%>/bbs/created_ok.do";
    	else if(mode=="update")
    		f.action="<%=cp%>/bbs/update_ok.do";

        f.submit();
    }
</script>
</head>
<body>

<div class="layoutMain">
	<div class="layoutHeader">
		<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
	</div>
	
	<div class="layoutBody">

		<div style="min-height: 450px;">
				<div style="width:100%;	height: 40px; line-height:40px;clear: both; border-top: 1px solid #DAD9FF;border-bottom: 1px solid #DAD9FF;">
				    <div style="width:600px; height:30px; line-height:30px; margin:5px auto;">
				        <img src="<%=cp%>/res/images/arrow.gif" alt="" style="padding-left: 5px; padding-right: 5px;">
				        <span style="font-weight: bold;font-size:13pt;font-family: 나눔고딕, 맑은 고딕, 굴림;">게시판</span>
				    </div>
				</div>
			
				<div style="margin: 10px auto; margin-top: 20px; width:600px; min-height: 400px;">
		
					<form name="boardForm" method="post">
					  <table style="width: 600px; margin: 0px auto; border-spacing: 0px;">
					  <tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					
					  <tr align="left" height="40"> 
					      <td width="100" bgcolor="#EEEEEE" style="text-align: center;">제&nbsp;&nbsp;&nbsp;&nbsp;목</td>
					      <td width="500" style="padding-left:10px;"> 
					        <input type="text" name="subject" size="75" maxlength="100" class="boxTF" value="${dto.subject}">
					      </td>
					  </tr>
					  <tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					  
					  <tr align="left" height="40"> 
					      <td width="100" bgcolor="#EEEEEE" style="text-align: center;">작 성 자</td>
					      <td width="500" style="padding-left:10px;"> 
					        ${sessionScope.member.userName}
					      </td>
					  </tr>
				      <tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
				      
					  <tr align="left"> 
					      <td width="100" bgcolor="#EEEEEE" style="text-align: center; padding-top:5px;" valign="top">내&nbsp;&nbsp;&nbsp;&nbsp;용</td>
					      <td width="500" valign="top" style="padding:5px 0px 5px 10px;"> 
					        <textarea name="content" cols="75" rows="12" class="boxTA">${dto.content}</textarea>
					      </td>
					  </tr>
					  <tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					  </table>
					
					  <table style="width: 600px; margin: 0px auto; border-spacing: 0px;">
					     <tr height="45"> 
					      <td align="center" >
					          <button type="button" class="btn" onclick="sendBoard();"> 확인 </button>
					          <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/bbs/list.do';"> 취소 </button>
					      
					         <c:if test="${mode=='update'}">
					         	 <input type="hidden" name="num" value="${dto.num}">
					        	 <input type="hidden" name="page" value="${page}">
					        </c:if>
					      </td>
					    </tr>
					  </table>
					</form>
				</div>
		</div>

    </div>
	
	<div class="layoutFooter">
		<jsp:include page="/WEB-INF/views/layout/footer.jsp"></jsp:include>
	</div>
</div>

<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery-ui.min.js"></script>
<script type="text/javascript" src="<%=cp%>/res/jquery/js/jquery.ui.datepicker-ko.js"></script>
</body>
</html>