<%@ page contentType="text/html; charset=UTF-8"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%
	String cp=request.getContextPath();
%>

<c:if test="${dataCount!=0}">
<table style='width: 600px; margin: 10px auto 30px; border-spacing: 0px;'>
<tr height="30">
    <td>
       <div style="clear: both;">
           <div style="float: left;"><span style="color: #3EA9CD; font-weight: bold;">댓글 ${dataCount}개</span> <span>[댓글 목록, ${pageNo}/${total_page} 페이지]</span></div>
           <div style="float: right; text-align: right;"></div>
       </div>
    </td>
</tr>

<c:forEach var="vo" items="${list}">
    <tr height='30' style='background: #eee;'>
       <td width='50%' style='padding:5px 5px;'>
           <span><b>${vo.userName}</b></span>
        </td>
       <td width='50%' style='padding:5px 5px;' align='right'>
           <span>${vo.created}</span> |
<c:if test="${sessionScope.member.userId==vo.userId || sessionScope.member.userId=='admin'}">		   
          <a onclick='deleteReply("${vo.replyNum}", "${pageNo}");'>삭제</a>
</c:if>		   
<c:if test="${sessionScope.member.userId!=vo.userId && sessionScope.member.userId!='admin'}">		   
          <a href='#'>신고</a>
</c:if>		   
        </td>
    </tr>
    <tr>
        <td colspan='2' valign='top' style='padding:5px 5px;'>
                ${vo.content}
        </td>
    </tr>
    <tr>
        <td style='padding:7px 5px;'>
            <button type='button' class='btn btnReplyAnswerLayout' data-replyNum='${vo.replyNum}'>답글 <span id="answerCount${vo.replyNum}">${vo.answerCount}</span></button>
        </td>
        <td style='padding:7px 5px;' align='right'>
            <button type='button' class='btn' >좋아요 <span>0</span></button>
            <button type='button' class='btn'>싫어요 <span>0</span></button>
        </td>
    </tr>

    <tr class='replyAnswer' style='display: none;'>
        <td colspan='2'>
            <div id='listReplyAnswer${vo.replyNum}' class='answerList' style='border-top: 1px solid #ccc;'></div>
            <div style='clear: both; padding: 10px 10px;'>
                <div style='float: left; width: 5%;'>└</div>
                <div style='float: left; width:95%'>
                    <textarea id='answerContent${vo.replyNum}' cols='72' rows='12' class='boxTA' style='width:537px; height: 70px;'></textarea>
                 </div>
            </div>
             <div style='padding: 0px 10px 10px; text-align: right;'>
                <button type='button' class='btn' onclick='sendReplyAnswer(${vo.replyNum});'>답글 등록</button>
            </div>
        
        </td>
    </tr>
</c:forEach>
     <tr height="35">
         <td colspan='2'>
              ${paging}
         </td>
     </tr>
</table>
</c:if>