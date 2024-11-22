package com.web.fitquest.mapper.board;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.web.fitquest.model.board.Board;
import com.web.fitquest.model.searchCondition.SearchCondition;

@Mapper
public interface BoardMapper {
    
    List<Board> allBoards(SearchCondition searchCondition);
	
	Board getBoard(int boardId);
	
	Integer addBoard(Board board);
	
	Integer updateBoard(Board board);
	
	Integer deleteBoard(int boardId);

	List<Board> searchBoardsByTitle(String searchText);

}
