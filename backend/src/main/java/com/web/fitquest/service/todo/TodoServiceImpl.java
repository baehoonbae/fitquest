package com.web.fitquest.service.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.fitquest.mapper.todo.TodoMapper;
import com.web.fitquest.model.todo.Todo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;

    @Override
    public Optional<List<Todo>> getTodoList(Todo todo) {
        return Optional.ofNullable(todoMapper.getTodoList(todo));
    }

    @Override
    public boolean addTodo(Todo todo) {
        return todoMapper.addTodo(todo) > 0;
    }

    @Override
    public boolean updateTodo(Todo todo) {
        return todoMapper.updateTodo(todo) > 0;
    }

    @Override
    public Optional<List<Todo>> getTodoListByYearAndUserId(Todo todo) {
        return Optional.ofNullable(todoMapper.getTodoListByYearAndUserId(todo));
    }

    @Override
    public Optional<Todo> getTodoById(int id) {
        return Optional.ofNullable(todoMapper.getTodoById(id));
    }

    @Override
    public boolean deleteTodo(int id) {
        return todoMapper.deleteTodo(id) > 0;
    }

    @Override
    public Optional<List<Todo>> getTodoListByYearAndMonth(Todo todo) {
        return Optional.ofNullable(todoMapper.getTodoListByYearAndMonth(todo));
    }
}
