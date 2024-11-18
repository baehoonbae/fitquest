package com.web.fitquest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.model.todo.Todo;
import com.web.fitquest.requests.TodoRequest;
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

    @GetMapping("/{date}/{userId}")
    public ResponseEntity<?> getTodoList(@PathVariable String date, @PathVariable int userId) {
        try {
            Todo todo = new Todo(0, userId, 0, 0, "", date);
            Optional<List<Todo>> opTodoList = todoService.getTodoList(todo);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    @PostMapping("")
    public ResponseEntity<?> addTodo(@RequestBody Todo todo) {
        try {
            boolean success = todoService.addTodo(todo);
            return success ? ResponseEntity.ok().body("todo 추가 성공") : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateTodo(@PathVariable int id, @RequestBody Todo todo) {
        try {
            boolean success = todoService.updateTodo(todo);
            return success ? ResponseEntity.ok().body("todo 수정 성공") : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // year 별 통계
    // year와 userId에 해당하는 모든 todo를 가져온다.
    @GetMapping("/statistics/{year}")
    public ResponseEntity<?> getStatistics(@PathVariable int year, @RequestBody TodoRequest todoRequest) {
        try {
            Todo todo = new Todo(0, todoRequest.getUserId(), 0, 0, "", year + "-01-01");
            Optional<List<Todo>> opTodoList = todoService.getTodoListByYearAndUserId(todo);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }
}
