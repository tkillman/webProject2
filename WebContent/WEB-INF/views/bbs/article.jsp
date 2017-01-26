<%@ page contentType="text/html; charset=UTF-8"%>
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
function deleteBoard() {
<c:if test="${sessionScope.member.userId=='admin' || sessionScope.member.userId==dto.userId}">
    var num = "${dto.num}";
    var page = "${page}";
    var params = "num="+num+"&page="+page;
    var url = "<%=cp%>/bbs/delete.do?" + params;

    if(confirm("위 자료를 삭제 하시 겠습니까 ? "))
    	location.href=url;
</c:if>    
<c:if test="${sessionScope.member.userId!='admin' && sessionScope.member.userId!=dto.userId}">
    alert("게시물을 삭제할 수  없습니다.");
</c:if>
}

function updateBoard() {
<c:if test="${sessionScope.member.userId==dto.userId}">
    var num = "${dto.num}";
    var page = "${page}";
    var params = "num="+num+"&page="+page;
    var url = "<%=cp%>/bbs/update.do?" + params;

    location.href=url;
</c:if>

<c:if test="${sessionScope.member.userId!=dto.userId}">
   alert("게시물을 수정할 수  없습니다.");
</c:if>
}

function login() {
	location.href="<%=cp%>/member/login.do";
}



// 댓글 리스트
$(function(){
	listPage(1);
});


function listPage(page) {
	var url="<%=cp%>/bbs/listReply.do";
	var num="${dto.num}";
	$.post(url, {num:num, pageNo:page}, function(data){
		$("#listReply").html(data);
	});
}


// 댓글 등록
function sendReply() {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return;
	}
	
	var num="${dto.num}";
	var content=$("#replyContent").val().trim();
	
	if(! content) {
		$("#replyContent").focus();
		return;
	}
	
	var query="num="+num;
	query+="&content="+content;
	query+="&answer=0";	
	
	$.ajax({
		type:"post"
		,url:"<%=cp%>/bbs/insertReply.do"
		,data:query
		,dataType:"json"
		,success:function(data) {
			$("#replyContent").val("");
			
			var isLogin=data.isLogin;
			// var state=data.state;
			if(isLogin=="false") {
				login();
			} else {
				listPage(1);
			}
		}
		,error:function(e) {
			console.log(e.responseText);
		}
	});
}

// 댓글별 답글 리스트
function listAnswer(answer) {
	var listReplyAnswerId="#listReplyAnswer"+answer;
	var url="<%=cp%>/bbs/listReplyAnswer.do";
	$.get(url, {answer:answer}, function(data){
		$(listReplyAnswerId).html(data);
	});
}

// 답글 버튼
$(function(){
	
	// $(".btnReplyAnswerLayout").click(function(){
	$("body").on("click", ".btnReplyAnswerLayout", function(){
		
		//답글이 달릴 곳, <tr class='replyAnswer' display:none>로 간다.
		var $trReplyAnswer = $(this).parent().parent().next();
		
		
		var $answerList = $trReplyAnswer.children().children().eq(0);
		
		
		//댓글의 상태
		var isVisible = $trReplyAnswer.is(':visible');
		
		//댓글 갯수
		var replyNum = $(this).attr("data-replyNum");
			
		
		
		if(isVisible) {
			$trReplyAnswer.hide();
		} else {
			$trReplyAnswer.show();
            
			// 답글 리스트
			listAnswer(replyNum);
		}
		
	});
});
	
	
// 댓글별 답글 등록
function sendReplyAnswer(replyNum) {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return;
	}
	
	
	var num="${dto.num}";
	var content=$("#answerContent"+replyNum).val().trim();
	if(! content) {
		$("#answerContent"+replyNum).focus();
		return;
	}
	
	
	var query="num="+num;
	query+="&content="+content;
	query+="&answer="+replyNum;	
	
	
	$.ajax({
		type:"post"
		,url:"<%=cp%>/bbs/insertReplyAnswer.do"
		,data:query
		,dataType:"json"
		,success:function(data) {
			$("#answerContent"+replyNum).val("");
			
			var isLogin=data.isLogin;
			// var state=data.state;
			if(isLogin=="false") {
				login();
			} else {
				// 답글 리스트
				listAnswer(replyNum);
				
				// 답글 개수
				countAnswer(replyNum);
			}
		}
		,error:function(e) {
			console.log(e.responseText);
		}
	});
}


// 댓글별 답글 개수
function countAnswer(answer) {
	var url="<%=cp%>/bbs/countReplyAnswer.do";
	$.post(url, {answer:answer}, function(data){
		var count=data.count;
		var answerCountId="#answerCount"+answer;
		
		$(answerCountId).html(count);
	}, "json");
}

// 댓글 삭제
function deleteReply(replyNum, page) {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return;
	}
	
	if(confirm("게시물을 삭제하시겠습니까 ? ")) {	
		var url="<%=cp%>/bbs/deleteReply.do";
		$.post(url, {replyNum:replyNum, userId:uid}, function(data){
		        var isLogin=data.isLogin;

				if(isLogin=="false") {
					login();
				} else {
					listPage(page);
				}
		}, "json");
	}
}


// 댓글별 답글 삭제
function deleteReplyAnswer(replyNum, answer) {
	var uid="${sessionScope.member.userId}";
	if(! uid) {
		login();
		return false;
	}
	
	if(confirm("게시물을 삭제하시겠습니까 ? ")) {	
		var url="<%=cp%>/bbs/deleteReplyAnswer.do";
		$.post(url, {replyNum:replyNum, userId:uid}, function(data){
		        var isLogin=data.isLogin;
				if(isLogin=="false") {
					login();
				} else {
				    listAnswer(answer);
				    countAnswer(answer);
				}
		}, "json");
	}
}

</script>

</head>
<body>

<div class="layoutMain">
	<div class="layoutHeader">
		<jsp:include page="/WEB-INF/views/layout/header.jsp"></jsp:include>
	</div>
	
	<div class="layoutBody">
	
		<div style="min-height: 400px;">
				<div style="width:100%;	height: 40px; line-height:40px;clear: both; border-top: 1px solid #DAD9FF;border-bottom: 1px solid #DAD9FF;">
				    <div style="width:600px; height:30px; line-height:30px; margin:5px auto;">
				        <img src="<%=cp%>/res/images/arrow.gif" alt="" style="padding-left: 5px; padding-right: 5px;">
				        <span style="font-weight: bold;font-size:13pt;font-family: 나눔고딕, 맑은 고딕, 굴림;">게시판</span>
				    </div>
				</div>
				
				<div style="margin: 20px auto 5px; width:600px; min-height: 350px;">
					<table style="width: 600px; margin: 0px auto; border-spacing: 0px;">
					<tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					
					<tr height="35">
					    <td colspan="2" align="center">
						   ${dto.subject}
					    </td>
					</tr>
					<tr><td colspan="2" height="1" bgcolor="#ccc" ></td></tr>
					
					<tr height="35">
					    <td width="50%" align="left" style="padding-left: 5px;">
   					        이름 : ${dto.userName}
					    </td>
					    <td width="50%" align="right" style="padding-right: 5px;">
    					    ${dto.created} | 조회 ${dto.hitCount}
					    </td>
					</tr>
					<tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					
					<tr>
					  <td colspan="2" align="left" style="padding: 10px 5px;" valign="top" height="150">
					        ${dto.content}
					   </td>
					</tr>
					<tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>

					<tr height="35">
 					   <td colspan="2" align="left" style="padding-left: 5px;">
  					        이전글 :
							<c:if test="${not empty preReadDto }">
								<a href="<%=cp%>/bbs/article.do?${params}&num=${preReadDto.num}">${preReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>

					<tr height="35">
  					  <td colspan="2" align="left" style="padding-left: 5px;">
  					        다음글 :
							<c:if test="${not empty nextReadDto }">
								<a href="<%=cp%>/bbs/article.do?${params}&num=${nextReadDto.num}">${nextReadDto.subject}</a>
							</c:if>
						</td>
					</tr>
					<tr><td colspan="2" height="1" bgcolor="#ccc"></td></tr>
					</table>
					
					<table style="width: 600px; margin: 0px auto; border-spacing: 0px;">
					<tr height="45">
					    <td width="50%" align="left">
		<c:if test="${sessionScope.member.userId==dto.userId}">	
		                      <button type="button" class="btn" onclick="updateBoard();">수정</button>		          
		</c:if>
		<c:if test="${sessionScope.member.userId==dto.userId || sessionScope.member.userId=='admin'}">
		                      <button type="button" class="btn" onclick="deleteBoard();">삭제</button>			          
		</c:if>
					    </td>
					
					    <td align="right">
                              <button type="button" class="btn" onclick="javascript:location.href='<%=cp%>/bbs/list.do?${params}';">리스트</button>
					    </td>
					</tr>
					</table>
		
				</div>
		</div>
		
		<div>
            <table style='width: 600px; margin: 15px auto 0px; border-spacing: 0px;'>
            <tr height='30'> 
	            <td colspan='2' align='left'>
	            	<span style='font-weight: bold;' >| 댓글쓰기</span><span> - 타인을 비방하거나 개인정보를 유출하는 글의 게시를 삼가 주세요.</span>
	            </td>
            </tr>
            <tr>
               <td colspan='2' style='padding:5px 5px 0px;'>
                    <textarea id='replyContent' cols='72' rows='12' class='boxTA' style='width:590px; height: 70px;'></textarea>
                </td>
            </tr>
            <tr>
               <td colspan='2' style='padding:0px 5px 15px;' align='right'>
                    <button type='button' class='btn' style='padding:10px 20px;' onclick="sendReply();">댓글 등록</button>
                </td>
            </tr>
            </table>
            
            <div id="listReply"></div>
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