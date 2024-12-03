package com.web.fitquest.service.activity;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.web.fitquest.mapper.activity.ActivityMapper;
import com.web.fitquest.mapper.todo.TodoMapper;
import com.web.fitquest.model.activity.Activity;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class ActivityServiceImpl implements ActivityService {
    private final ActivityMapper activityMapper;
    private final TodoMapper todoMapper;

    @Override
    @Async
    public CompletableFuture<Map<String, Double>> getActivityRatios(Activity activity) {
        List<Activity> activities = activityMapper.getActivityRatios(activity);
        int year = Integer.parseInt(activity.getDate().split("-")[0]);
        LocalDate startDate = LocalDate.of(year, 1, 1);
        LocalDate endDate = LocalDate.of(year, 12, 31);
        Map<String, Double> result = new HashMap<>();

        // 모든 날짜에 대해 기본값 0.0 설정
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            result.put(currentDate.toString(), 0.0);
            currentDate = currentDate.plusDays(1);
        }

        // 활동 데이터를 Map에 저장
        activities.forEach(act -> result.put(act.getDate(), act.getRatio()));

        return CompletableFuture.completedFuture(result);
    }

    @Override
    @Async
    public CompletableFuture<Boolean> updateActivityRatio(Activity activity) {
        try {
            boolean result = activityMapper.insertOrUpdateActivity(activity) > 0;
            return CompletableFuture.completedFuture(result);
        } catch (Exception e) {
            log.error("Activity ratio update failed: {}", e.getMessage());
            return CompletableFuture.completedFuture(false);
        }
    }

    @Override
    @Async
    public CompletableFuture<Double> updateDailyActivity(int userId, String date) {
        double ratio = todoMapper.getDailyCompletionRatio(userId, date);
        Activity activity = new Activity(0, userId, date, ratio);
        activityMapper.insertOrUpdateActivity(activity);
        
        return CompletableFuture.completedFuture(ratio);
    }
}
