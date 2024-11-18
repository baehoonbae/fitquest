package com.web.fitquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.todo.TodoService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/todo")
@Slf4j
public class TodoController {

    private TodoService todoService;

    @Autowired
    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }
}
