package com.web.fitquest.service.activity;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import com.web.fitquest.model.activity.Activity;

public interface ActivityService {
    CompletableFuture<Map<String, Double>> getActivityRatios(Activity activity);
    CompletableFuture<Boolean> updateActivityRatio(Activity activity);
    CompletableFuture<Double> updateDailyActivity(int userId, String date);
}
