package com.web.fitquest.service.activity;

import java.util.Map;
import java.util.Optional;

import com.web.fitquest.model.activity.Activity;

public interface ActivityService {
    Optional<Map<String, Double>> getActivityRatios(Activity activity);
    boolean updateActivityRatio(Activity activity);
    double updateDailyActivity(int userId, String date);
}
