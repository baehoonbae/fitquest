package com.web.fitquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.comment.CommentService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/comment")
@Slf4j
public class CommentController {

    private CommentService commentService;

    @Autowired
    public CommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    //CRUD
}
