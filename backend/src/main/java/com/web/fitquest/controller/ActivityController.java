package com.web.fitquest.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.activity.ActivityService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/activity")
@RequiredArgsConstructor
@Slf4j
public class ActivityController {
    private final ActivityService activityService;

}
