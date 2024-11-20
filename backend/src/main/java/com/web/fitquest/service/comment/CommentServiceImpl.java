package com.web.fitquest.service.comment;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.fitquest.mapper.comment.CommentMapper;
import com.web.fitquest.model.comment.Comment;
import lombok.extern.slf4j.Slf4j;


@Service
@Slf4j
public class CommentServiceImpl implements CommentService {

    private final CommentMapper commentMapper;

    public CommentServiceImpl(CommentMapper commentMapper){
        this.commentMapper = commentMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<List<Comment>> allComments(int boardId) {
        return Optional.ofNullable(commentMapper.allComments(boardId));
    }

    @Override
    @Transactional
    public Optional<Comment> addComment(Comment comment) {
        try {
            int result = commentMapper.addComment(comment);
            if(result > 0) {
                // INSERT 성공 시 생성된 댓글 반환
                return Optional.of(comment);  // useGeneratedKeys로 id가 설정됨
            }
            return Optional.empty();
        } catch(Exception e) {
            log.error("댓글 추가 실패: ", e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional
    public Optional<Comment> deleteComment(Comment comment) {
        try {
            int result = commentMapper.deleteComment(comment);
            if(result > 0) {
                return Optional.of(comment);
            }
            return Optional.empty();
        } catch(Exception e) {
            log.error("댓글 삭제 실패: ", e);
            return Optional.empty();
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Comment> getComment(int id) {  // commentId -> id
        return Optional.ofNullable(commentMapper.getComment(id));
    }
}