package com.bbs;

import java.util.List;

public interface BoardDAO {
	
	public int insertBoard(BoardDTO dto);
	public int dataCount();
	public int dataCount(String searchKey, String searchValue);
	public List<BoardDTO> listBoard(int start, int end);
	public List<BoardDTO> listBoard(int start, int end, String searchKey, String searchValue);	
	public int updateHitCount(int num);
	public BoardDTO readBoard(int num);
	public BoardDTO preReadBoard(int num, String searchKey, String searchValue);
    public BoardDTO nextReadBoard(int num, String searchKey, String searchValue);	
	public int updateBoard(BoardDTO dto);
	public int deleteBoard(int num);	
	
	public int insertReply(ReplyDTO dto);
	public int dataCountReply(int num);
	public List<ReplyDTO> listReply(int num, int start, int end);
	public int deleteReply(int replyNum);
	
	
	
	
	//댓글에 대한 답글, 여기서부터 보기
	public List<ReplyDTO> listReplyAnswer(int answer);
	public int dataCountReplyAnswer(int answer);
	public int deleteReplyAnswer(int replyNum);
}
