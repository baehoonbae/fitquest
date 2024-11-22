package com.web.fitquest.service.board;

import java.util.List;
import java.util.Optional;

import com.web.fitquest.model.searchCondition.SearchCondition;
import com.web.fitquest.model.board.Board;

public interface BoardService {
    
    Optional<List<Board>> allBoards(SearchCondition searchCondition);
	
	Optional<Board> getBoard(int boardId);
	
	Optional<Integer> addBoard(Board board);
	
	Optional<Integer> updateBoard(Board board);
	
	Optional<Integer> deleteBoard(int boardId);

	Optional<List<Board>> searchBoardsByTitle(String searchText);

}
