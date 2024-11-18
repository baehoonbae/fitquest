package com.web.fitquest.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.hit.HitService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/hit")
@Slf4j
public class HitController {
    
    private HitService hitService;

    @Autowired
    public HitController(HitService hitService) {
        this.hitService = hitService;
    }
    
}
