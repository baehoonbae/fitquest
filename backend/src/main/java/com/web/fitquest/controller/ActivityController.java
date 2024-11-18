package com.web.fitquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.activity.ActivityService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/activity")
@Slf4j
public class ActivityController {
        private ActivityService activityService;

    @Autowired
    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }
}
