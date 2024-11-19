package com.web.fitquest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.model.board.Board;
import com.web.fitquest.model.searchCondition.SearchCondition;
import com.web.fitquest.service.board.BoardService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/board")
@RequiredArgsConstructor
@Slf4j
public class BoardController {
   
   private final BoardService boardService;

   @GetMapping
   public ResponseEntity<?> getAllBoards(@ModelAttribute SearchCondition searchCondition) {
       log.debug("BoardController/getAllBoards");
       try {
           Optional<List<Board>> result = boardService.allBoards(searchCondition);
           if(result.isPresent() && !result.get().isEmpty()) {
               return new ResponseEntity<List<Board>>(result.get(), HttpStatus.OK);
           } else {
               return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
           }
       } catch(Exception e) {
            return exceptionHandling(e);
       }
   }
   
   // 특정 게시글 조회
   @GetMapping("/{id}")
   public ResponseEntity<?> getBoard(@PathVariable int id) {
       log.debug("BoardController/getBoard");
       try {
           Optional<Board> result = boardService.getBoard(id);
           if(result.isPresent()) {
               return new ResponseEntity<Board>(result.get(), HttpStatus.OK);
           } else {
               return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
           }
       } catch(Exception e) {
           return exceptionHandling(e);
       }
   }
   
   // 게시글 작성
   @PostMapping
   public ResponseEntity<?> addBoard(@RequestBody Board board) {
       log.debug("BoardController/addBoard");
       try {
           Optional<Integer> result = boardService.addBoard(board);
           if(result.isPresent() && result.get() > 0) {
               return new ResponseEntity<Board>(board, HttpStatus.CREATED);
           } else {
               return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
           }
       } catch(Exception e) {
           return exceptionHandling(e);
       }
   }
   
   // 게시글 수정
   @PutMapping("/{id}")
   public ResponseEntity<?> updateBoard(@PathVariable int id, @RequestBody Board board) {
       log.debug("BoardController/updateBoard");
       try {
           board.setId(id);
           Optional<Integer> result = boardService.updateBoard(board);
           if(result.isPresent() && result.get() > 0) {
               return new ResponseEntity<Board>(board, HttpStatus.OK);
           } else {
               return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
           }
       } catch(Exception e) {
           return exceptionHandling(e);
       }
   }
   
   // 게시글 삭제
   @DeleteMapping("/{id}")
   public ResponseEntity<?> deleteBoard(@PathVariable int id) {
       log.debug("BoardController/deleteBoard");
       try {
           Optional<Integer> result = boardService.deleteBoard(id);
           if(result.isPresent() && result.get() > 0) {
               return new ResponseEntity<Integer>(result.get(), HttpStatus.OK);
           } else {
               return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
           }
       } catch(Exception e) {
           return exceptionHandling(e);
       }
   }
   
   private ResponseEntity<String> exceptionHandling(Exception e) {
       e.printStackTrace();
       return new ResponseEntity<String>("Error : " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
   }
}