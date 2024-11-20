package com.web.fitquest.service.board;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.fitquest.mapper.board.BoardMapper;
import com.web.fitquest.model.board.Board;
import com.web.fitquest.model.searchCondition.SearchCondition;

@Service
public class BoardServiceImpl implements BoardService {

    private final BoardMapper boardMapper;

    public BoardServiceImpl(BoardMapper boardMapper) {
        this.boardMapper = boardMapper;
    }

	@Override
	@Transactional
	public Optional<List<Board>> allBoards(SearchCondition searchCondition) {
		return Optional.ofNullable(boardMapper.allBoards(searchCondition));
	}

	@Override
	@Transactional
	public Optional<Board> getBoard(int boardId) {
		return Optional.ofNullable(boardMapper.getBoard(boardId));
	}

	@Override
	@Transactional
	public Optional<Integer> addBoard(Board board) {
		return Optional.ofNullable(boardMapper.addBoard(board));
	}

	@Override
	@Transactional
	public Optional<Integer> updateBoard(Board board) {
		return Optional.ofNullable(boardMapper.updateBoard(board));
	}

	@Override
	@Transactional
	public Optional<Integer> deleteBoard(int boardId) {
		return Optional.ofNullable(boardMapper.deleteBoard(boardId));
	}

}
