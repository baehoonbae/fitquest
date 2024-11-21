package com.web.fitquest.controller;

import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.model.activity.Activity;
import com.web.fitquest.service.activity.ActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {
    private final ActivityService activityService;

    // 1년치 활동 데이터 조회 (완료된 todo 비율)
    @GetMapping("/{year}/{userId}")
    public ResponseEntity<Map<String, Double>> getActivities(@PathVariable int year, @PathVariable int userId) {
        try {
            Activity activity = new Activity(0, userId, year + "-01-01", 0);
            Optional<Map<String, Double>> opActivities = activityService.getActivityRatios(activity);
            return opActivities.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            log.error("Error getting activities: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    // 특정 날짜의 activity만 갱신
    @PutMapping("/daily/{date}/{userId}")
    public ResponseEntity<Double> updateDailyActivity(@PathVariable String date, @PathVariable int userId) {
        try {
            double ratio = activityService.updateDailyActivity(userId, date);
            return ResponseEntity.ok(ratio);
        } catch (Exception e) {
            log.error("Error updating daily activity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}
