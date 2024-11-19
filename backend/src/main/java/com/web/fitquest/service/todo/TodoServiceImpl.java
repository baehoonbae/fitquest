package com.web.fitquest.service.todo;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.web.fitquest.mapper.todo.TodoMapper;
import com.web.fitquest.model.activity.Activity;
import com.web.fitquest.model.todo.Todo;
import com.web.fitquest.service.activity.ActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class TodoServiceImpl implements TodoService {

    private final TodoMapper todoMapper;
    private final ActivityService activityService;

    private void updateActivityRatio(int userId, String date) {
        double ratio = todoMapper.getDailyCompletionRatio(userId, date);
        Activity activity = new Activity(0, userId, date, ratio);
        activityService.updateActivityRatio(activity);
    }

    @Override
    public Optional<List<Todo>> getTodoList(Todo todo) {
        return Optional.ofNullable(todoMapper.getTodoList(todo));
    }

    @Override
    public boolean addTodo(Todo todo) {
        boolean success = todoMapper.addTodo(todo) > 0;
        if (success) {
            updateActivityRatio(todo.getUserId(), todo.getDate());
        }
        return success;
    }

    @Override
    public boolean updateTodo(Todo todo) {
        boolean success = todoMapper.updateTodo(todo) > 0;
        if (success) {
            updateActivityRatio(todo.getUserId(), todo.getDate());
        }
        return success;
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
        Todo todo = todoMapper.getTodoById(id);
        boolean success = todoMapper.deleteTodo(id) > 0;
        if (success && todo != null) {
            updateActivityRatio(todo.getUserId(), todo.getDate());
        }
        return success;
    }

    @Override
    public Optional<List<Todo>> getTodoListByYearAndMonth(Todo todo) {
        return Optional.ofNullable(todoMapper.getTodoListByYearAndMonth(todo));
    }
}
