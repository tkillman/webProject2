package com.bbs;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.member.SessionInfo;
import com.util.MyServlet;
import com.util.MyUtil;

import net.sf.json.JSONObject;

@WebServlet("/bbs/*")
public class BoardServlet extends MyServlet {
	private static final long serialVersionUID = 1L;

	@Override
	protected void process(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		req.setCharacterEncoding("utf-8");
		
		String uri=req.getRequestURI();
		
		// uri�� ���� �۾� ����
		if(uri.indexOf("list.do")!=-1) {
			list(req, resp);
		} else if(uri.indexOf("created.do")!=-1) {
			createdForm(req, resp);
		} else if(uri.indexOf("created_ok.do")!=-1) {
			createdSubmit(req, resp);
		} else if(uri.indexOf("article.do")!=-1) {
			article(req, resp);
		} else if(uri.indexOf("update.do")!=-1) {
			updateForm(req, resp);
		} else if(uri.indexOf("update_ok.do")!=-1) {
			updateSubmit(req, resp);
		} else if(uri.indexOf("delete.do")!=-1) {
			delete(req, resp);
		} else if(uri.indexOf("insertReply.do")!=-1) {
			insertReply(req, resp);
		} else if(uri.indexOf("listReply.do")!=-1) {
			listReply(req, resp);
		} else if(uri.indexOf("deleteReply.do")!=-1) {
			deleteReply(req, resp);
		} else if(uri.indexOf("insertReplyAnswer.do")!=-1) {
			insertReplyAnswer(req, resp);
		} else if(uri.indexOf("listReplyAnswer.do")!=-1) {
			listReplyAnswer(req, resp);
		} else if(uri.indexOf("deleteReplyAnswer.do")!=-1) {
			deleteReplyAnswer(req, resp);
		} else if(uri.indexOf("countReplyAnswer.do")!=-1) {
			countReplyAnswer(req, resp);
		} 
	}

	private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �Խù� ����Ʈ
		String cp = req.getContextPath();

		BoardDAO dao = new BoardDAOImpl();
		MyUtil util = new MyUtil();
	
		String page=req.getParameter("page");
		int current_page=1;
		if(page!=null)
			current_page=Integer.parseInt(page);
		
		// �˻�
		String searchKey=req.getParameter("searchKey");
		String searchValue=req.getParameter("searchValue");
		if(searchKey==null) {
			searchKey="subject";
			searchValue="";
		}
		// GET ����� ��� ���ڵ�
		if(req.getMethod().equalsIgnoreCase("GET")) {
			searchValue=URLDecoder.decode(searchValue, "utf-8");
		}
		
		// ��ü ������ ����
		int dataCount;
		if(searchValue.length()==0)
			dataCount=dao.dataCount();
		else
			dataCount=dao.dataCount(searchKey, searchValue);
		
		// ��ü ������ ��
		int numPerPage=10;
		int total_page=util.pageCount(numPerPage, dataCount);
		
		if(current_page>total_page)
			current_page=total_page;
		
		// �Խù� ������ ���۰� ��
		int start=(current_page-1)*numPerPage+1;
		int end=current_page*numPerPage;
		
		// �Խù� ��������
		List<BoardDTO> list=null;
		if(searchValue.length()==0)
			list=dao.listBoard(start, end);
		else
			list=dao.listBoard(start, end, searchKey, searchValue);
		
		// ����Ʈ �۹�ȣ �����
		int listNum, n=0;
		Iterator<BoardDTO>it=list.iterator();
		while(it.hasNext()) {
			BoardDTO dto=it.next();
			listNum=dataCount-(start+n-1);
			dto.setListNum(listNum);
			n++;
		}
		
		String params="";
		if(searchValue.length()!=0) {
			// �˻��� ��� �˻��� ���ڵ�
			searchValue=URLEncoder.encode(searchValue, "utf-8");
			params="searchKey="+searchKey+
					 "&searchValue="+searchValue;
		}
		
		// ����¡ ó��
		String listUrl=cp+"/bbs/list.do";
		String articleUrl=cp+"/bbs/article.do?page="+current_page;
		if(params.length()!=0) {
			listUrl+="?"+params;
			articleUrl+="&"+params;
		}
		
		String paging=util.paging(current_page, total_page, listUrl);
		
		// �������� JSP�� �ѱ� �Ӽ�
		req.setAttribute("list", list);
		req.setAttribute("page", current_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("paging", paging);
		req.setAttribute("articleUrl", articleUrl);
		
		// JSP�� ������
		String path="/WEB-INF/views/bbs/list.jsp";
		forward(req, resp, path);
	}
	
	private void createdForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �۾��� ��
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		
		req.setAttribute("mode", "created");
		String path="/WEB-INF/views/bbs/created.jsp";
		forward(req, resp, path);
	}
	
	private void createdSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �� ����
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}

		BoardDAO dao = new BoardDAOImpl();
		BoardDTO dto=new BoardDTO();
		
		// userId�� ���ǿ� ����� ����
		dto.setUserId(info.getUserId());
		
		// �Ķ����
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));
		
		dao.insertBoard(dto);
		
		resp.sendRedirect(cp+"/bbs/list.do");
	}

	private void article(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// �ۺ���
		String cp = req.getContextPath();
		BoardDAO dao = new BoardDAOImpl();
	
		// �Ķ���� : num, page, [searchKey, searchValue]
		int num=Integer.parseInt(req.getParameter("num"));
		String page=req.getParameter("page");
		String searchKey=req.getParameter("searchKey");
		String searchValue=req.getParameter("searchValue");
		if(searchKey==null) {
			searchKey="subject";
			searchValue="";
		}
		
		searchValue=URLDecoder.decode(searchValue, "utf-8");
		
		// ��ȸ�� ����
		dao.updateHitCount(num);
		
		// �Խù� ��������
		BoardDTO dto=dao.readBoard(num);
		if(dto==null) { // �Խù��� ������ �ٽ� ����Ʈ��
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		
		// ������ ������
		BoardDTO preReadDto=dao.preReadBoard(dto.getNum(), searchKey, searchValue);
		BoardDTO nextReadDto=dao.nextReadBoard(dto.getNum(), searchKey, searchValue);
		
		// ����Ʈ�� ������/�����ۿ��� ����� �Ķ����
		String params="page="+page;
		if(searchValue.length()!=0) {
			params+="&searchKey="+searchKey
					+"&searchValue="+URLEncoder.encode(searchValue, "utf-8");
		}
		
		// JSP�� ������ �Ӽ�
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("params", params);
		req.setAttribute("preReadDto", preReadDto);
		req.setAttribute("nextReadDto", nextReadDto);
		
		req.setAttribute("searchKey", searchKey);
		req.setAttribute("searchValue", searchValue);
		
		// ������
		String path="/WEB-INF/views/bbs/article.jsp";
		forward(req, resp, path);
	}
	
	private void updateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� ��
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}

		BoardDAO dao = new BoardDAOImpl();
	
		String page=req.getParameter("page");
		int num=Integer.parseInt(	req.getParameter("num"));
		BoardDTO dto=dao.readBoard(num);
		
		if(dto==null) {
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		// �Խù��� �ø� ����ڰ� �ƴϸ�
		if(! dto.getUserId().equals(info.getUserId())) {
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		req.setAttribute("dto", dto);
		req.setAttribute("page", page);
		req.setAttribute("mode", "update");
		
		String path="/WEB-INF/views/bbs/created.jsp";
		forward(req, resp, path);
	}

	private void updateSubmit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� �Ϸ�
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}
		BoardDAO dao = new BoardDAOImpl();
	
		String page=req.getParameter("page");
		
		if(req.getMethod().equalsIgnoreCase("GET")) {
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		BoardDTO dto=new BoardDTO();
		dto.setNum(Integer.parseInt(req.getParameter("num")));
		dto.setSubject(req.getParameter("subject"));
		dto.setContent(req.getParameter("content"));
		
		dao.updateBoard(dto);
		
		resp.sendRedirect(cp+"/bbs/list.do?page="+page);
	}

	private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ����
		String cp=req.getContextPath();
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		if(info==null) { // �α��ε��� ���� ���
			resp.sendRedirect(cp+"/member/login.do");
			return;
		}

		BoardDAO dao = new BoardDAOImpl();
	
		String page=req.getParameter("page");
		int num=Integer.parseInt(req.getParameter("num"));
		BoardDTO dto=dao.readBoard(num);
		
		if(dto==null) {
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		// �Խù��� �ø� ����ڳ� admin�� �ƴϸ�
		if(! dto.getUserId().equals(info.getUserId()) && ! info.getUserId().equals("admin")) {
			resp.sendRedirect(cp+"/bbs/list.do?page="+page);
			return;
		}
		
		// bbsReply ���̺��� ON DELETE CASCADE �ɼ����� bbs ���̺��� �����Ͱ� �������� �ڵ� ������
		dao.deleteBoard(num);
		resp.sendRedirect(cp+"/bbs/list.do?page="+page);
	}

	private void insertReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� �����ϱ� ---------------------------------------
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		BoardDAO dao = new BoardDAOImpl();
		
		String isLogin="true";
		String state="false";
		if(info==null) {
			isLogin="false";
		} else {
			int num = Integer.parseInt(req.getParameter("num"));
			ReplyDTO rdto = new ReplyDTO();
			rdto.setNum(num);
			rdto.setUserId(info.getUserId());
			rdto.setContent(req.getParameter("content"));

			int result=dao.insertReply(rdto);
			if(result==1)
				state="true";
		}
		
		JSONObject job=new JSONObject();
		job.put("isLogin", isLogin);
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print(job.toString());
	}	

	private void listReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� ����Ʈ(AJAX:TEXT) ---------------------------------------
		BoardDAO dao = new BoardDAOImpl();
		MyUtil util = new MyUtil();
		
		int num = Integer.parseInt(req.getParameter("num"));
		String pageNo = req.getParameter("pageNo");
		int current_page = 1;
		if (pageNo != null)
			current_page = Integer.parseInt(pageNo);

		int numPerPage = 5;
		int total_page = 0;
		int dataCount = 0;

		dataCount = dao.dataCountReply(num);
		total_page = util.pageCount(numPerPage, dataCount);
		if (current_page > total_page)
			current_page = total_page;

		int start = (current_page - 1) * numPerPage + 1;
		int end = current_page * numPerPage;

		// ����Ʈ�� ����� ������
		List<ReplyDTO> list = dao.listReply(num, start, end);

		// ���͸� <br>
		Iterator<ReplyDTO> it = list.iterator();
		while (it.hasNext()) {
			ReplyDTO dto = it.next();
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}

		// ����¡ó��(�μ�2�� ¥�� js�� ó��)
		String paging = util.paging(current_page, total_page);

		req.setAttribute("list", list);
		req.setAttribute("pageNo", current_page);
		req.setAttribute("dataCount", dataCount);
		req.setAttribute("total_page", total_page);
		req.setAttribute("paging", paging);

		// ������
		String path = "/WEB-INF/views/bbs/listReply.jsp";
		forward(req, resp, path);
	}	
	
	private void deleteReply(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ���� ���� ---------------------------------------
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		BoardDAO dao = new BoardDAOImpl();		
		int replyNum = Integer.parseInt(req.getParameter("replyNum"));
		String userId=req.getParameter("userId");
		
		String isLogin="true";
		String state="false";
		if(info==null) {
			isLogin="false";
		} if(info.getUserId().equals("admin") || info.getUserId().equals(userId)) {
			dao.deleteReply(replyNum);
			state="true";
		}
		
		JSONObject job=new JSONObject();
		job.put("isLogin", isLogin);
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print(job.toString());
	}

	private void insertReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��� �����ϱ� ---------------------------------------
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		BoardDAO dao = new BoardDAOImpl();		
		
		String isLogin="true";
		String state="false";
		if(info==null) {
			isLogin="false";
		} else {
			int num = Integer.parseInt(req.getParameter("num"));
			int answer = Integer.parseInt(req.getParameter("answer"));
			ReplyDTO rdto = new ReplyDTO();
			rdto.setNum(num);
			rdto.setUserId(info.getUserId());
			rdto.setContent(req.getParameter("content"));
			rdto.setAnswer(answer);

			int result=dao.insertReply(rdto);
			if(result==1)
				state="true";
		}
		
		JSONObject job=new JSONObject();
		job.put("isLogin", isLogin);
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print(job.toString());
	}	

	private void listReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��� ����Ʈ(AJAX:TEXT) ---------------------------------------
		BoardDAO dao = new BoardDAOImpl();
		
		int answer = Integer.parseInt(req.getParameter("answer"));
		List<ReplyDTO> list = dao.listReplyAnswer(answer);

		// ���͸� <br>(��Ÿ�� => style="white-space:pre;")
		Iterator<ReplyDTO> it = list.iterator();
		while (it.hasNext()) {
			ReplyDTO dto = it.next();
			dto.setContent(dto.getContent().replaceAll("\n", "<br>"));
		}
		
		req.setAttribute("list", list);

		String path = "/WEB-INF/views/bbs/listReplyAnswer.jsp";
		forward(req, resp, path);
	}	
	
	private void deleteReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��� ���� ---------------------------------------
		HttpSession session=req.getSession();
		SessionInfo info=(SessionInfo)session.getAttribute("member");
		BoardDAO dao = new BoardDAOImpl();		
		
		int replyNum = Integer.parseInt(req.getParameter("replyNum"));
		String userId=req.getParameter("userId");
		
		String isLogin="true";
		String state="false";
		if(info==null) {
			isLogin="false";
		} else if(info.getUserId().equals("admin") || info.getUserId().equals(userId)) {
			dao.deleteReplyAnswer(replyNum);
			state="true";
		}
		
		JSONObject job=new JSONObject();
		job.put("isLogin", isLogin);
		job.put("state", state);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print(job.toString());
	}
	
	private void countReplyAnswer(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		// ��� ���� ---------------------------------------
		BoardDAO dao = new BoardDAOImpl();
		int answer = Integer.parseInt(req.getParameter("answer"));
		
		int count=dao.dataCountReplyAnswer(answer);
		
		JSONObject job=new JSONObject();
		job.put("count", count);
		
		resp.setContentType("text/html;charset=utf-8");
		PrintWriter out=resp.getWriter();
		out.print(job.toString());
	}	
	
}
