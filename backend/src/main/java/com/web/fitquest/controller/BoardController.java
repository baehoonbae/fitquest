package com.web.fitquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.board.BoardService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@Slf4j
public class BoardController {

    private BoardService boardService;

    @Autowired
    public BoardController(BoardService boardService) {
        this.boardService = boardService;
    }

    //CRUD
}
