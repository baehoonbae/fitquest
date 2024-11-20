package com.web.fitquest.controller;

import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.web.fitquest.model.comment.Comment;
import com.web.fitquest.service.comment.CommentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/comment")
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;
    
    // 특정 게시글의 모든 댓글 조회
    @GetMapping("/board/{boardId}")  // URL 경로 수정
    public ResponseEntity<?> getAllComments(@PathVariable int boardId) {
        try {
            Optional<List<Comment>> result = commentService.allComments(boardId);
            if(result.isPresent() && !result.get().isEmpty()) {
                return new ResponseEntity<List<Comment>>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch(Exception e) {
            return exceptionHandling(e);
        }
    }

    // 특정 댓글 조회
    @GetMapping("/detail/{id}")  // URL 경로 중복을 피하기 위해 'detail' 추가
    public ResponseEntity<?> getComment(@PathVariable int commentId) {
        try {
            Optional<Comment> result = commentService.getComment(commentId);
            if(result.isPresent()) {
                return new ResponseEntity<Comment>(result.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
            }
        } catch(Exception e) {
            return exceptionHandling(e);
        }
    }

    // 새 댓글 추가 (일반 댓글 or 대댓글)
    @PostMapping
    public ResponseEntity<?> addComment(@RequestBody Comment comment) {
        try {
            // 필수 필드 검증 (기본 댓글 정보)
            if (comment.getBoardId() <= 0 ||
                comment.getUserId() <= 0 ||
                comment.getWriter() == null || comment.getWriter().trim().isEmpty() ||
                comment.getContent() == null || comment.getContent().trim().isEmpty()) {
                return new ResponseEntity<String>("필수 입력값이 누락되었습니다.", HttpStatus.BAD_REQUEST);
            }
            
            // 대댓글인 경우 추가 검증 (parentId null 체크 추가)
            if (comment.getParentId() != null && comment.getParentId() > 0) {
                Optional<Comment> parentComment = commentService.getComment(comment.getParentId());
                if (!parentComment.isPresent()) {
                    return new ResponseEntity<String>("부모 댓글이 존재하지 않습니다.", HttpStatus.BAD_REQUEST);
                }
                // 동일 게시글 내의 댓글에만 대댓글 작성 가능
                if (parentComment.get().getBoardId() != comment.getBoardId()) {
                    return new ResponseEntity<String>("해당 게시글의 댓글에만 대댓글을 작성할 수 있습니다.", HttpStatus.BAD_REQUEST);
                }
            }
            
            Optional<Comment> result = commentService.addComment(comment);
            if (result.isPresent()) {
                // 생성된 댓글 정보를 응답에 포함
                return new ResponseEntity<Comment>(result.get(), HttpStatus.CREATED);
            } else {
                return new ResponseEntity<String>("댓글 작성에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } catch(Exception e) {
            return exceptionHandling(e);
        }
    }

    // 댓글 삭제 (is_delete 플래그 설정)
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteComment(@PathVariable int id) {
        try {
            Comment comment = new Comment();
            comment.setId(id);
            Optional<Comment> result = commentService.deleteComment(comment);
            if (result.isPresent()) {
                return new ResponseEntity<Void>(HttpStatus.OK);
            } else {
                return new ResponseEntity<String>("댓글 삭제에 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
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