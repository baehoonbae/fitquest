package com.web.fitquest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.model.todo.Todo;
import com.web.fitquest.requests.TodoRequest;
import com.web.fitquest.service.todo.TodoService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/todo")
@RequiredArgsConstructor
@Slf4j
public class TodoController {

    private final TodoService todoService;

    // 날짜와 userId에 해당하는 모든 todo를 가져온다.
    @GetMapping("/{date}/{userId}")
    public ResponseEntity<?> getTodoList(@PathVariable String date, @PathVariable int userId) {
        try {
            Todo todo = new Todo(0, userId, 0, 0, "", date, 0);
            Optional<List<Todo>> opTodoList = todoService.getTodoList(todo);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // todo를 추가한다.
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

    // todo를 수정한다.
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
            Todo todo = new Todo(0, todoRequest.getUserId(), 0, 0, "", year + "-01-01", 0);
            Optional<List<Todo>> opTodoList = todoService.getTodoListByYearAndUserId(todo);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // id에 해당하는 todo를 가져온다.
    @GetMapping("/{id}")
    public ResponseEntity<?> getTodo(@PathVariable int id) {
        try {
            Optional<Todo> opTodo = todoService.getTodoById(id);
            return opTodo.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // id에 해당하는 todo를 삭제한다.
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteTodo(@PathVariable int id) {
        try {
            boolean success = todoService.deleteTodo(id);
            return success ? ResponseEntity.ok().body("todo 삭제 성공") : ResponseEntity.notFound().build();
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // year와 month에 해당하는 모든 todo를 가져온다.
    @GetMapping("/{userId}/{year}/{month}")
    public ResponseEntity<?> getTodoListByYearAndMonth(
            @PathVariable int userId,
            @PathVariable String year,
            @PathVariable String month) {
        try {
            Todo todo = new Todo(0, userId, 0, 0, "", year + "-" + month, 0);
            Optional<List<Todo>> opTodoList = todoService.getTodoListByYearAndMonth(todo);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // 카테고리 id에 해당하는 모든 todo를 가져온다.
    @GetMapping("/list/{categoryId}/{userId}")
    public ResponseEntity<?> getTodoListByCategoryId(@PathVariable int categoryId, @PathVariable int userId) {
        try {
            Optional<List<Todo>> opTodoList = todoService.getTodoListByCategoryId(categoryId, userId);
            return opTodoList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) { 
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }
}
