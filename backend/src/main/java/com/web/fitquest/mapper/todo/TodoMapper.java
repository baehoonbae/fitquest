package com.web.fitquest.mapper.todo;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.web.fitquest.model.todo.Todo;

@Mapper
public interface TodoMapper {
    List<Todo> getTodoList(Todo todo);
    int addTodo(Todo todo);
    int updateTodo(Todo todo);
    List<Todo> getTodoListByYearAndUserId(Todo todo);
    Todo getTodoById(int id);
}
