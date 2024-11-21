package com.web.fitquest.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.web.fitquest.model.hit.Hit;
import com.web.fitquest.service.hit.HitService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("api/hit")
@RequiredArgsConstructor
@Slf4j
public class HitController {

   private final HitService hitService;

   // 좋아요 토글
   @PostMapping("/{boardId}/{userId}")
   public ResponseEntity<Map<String, Object>> toggleHit(
           @PathVariable int boardId,
           @PathVariable int userId) {

       Map<String, Object> response = new HashMap<>();

       try {
           // 현재 좋아요 상태 확인
           boolean wasHit = hitService.isHitByUser(boardId, userId)
                   .orElse(false);

           if (wasHit) {
               // 좋아요가 있으면 제거
               hitService.removeHit(boardId, userId);
               response.put("action", "removed");
               response.put("isHit", false);
           } else {
               // 좋아요가 없으면 추가
               Hit hit = new Hit(0, boardId, userId);
               hitService.addHit(hit);
               response.put("action", "added");
               response.put("isHit", true);
           }

           // 현재 좋아요 수 조회
           int currentCount = hitService.getHitCount(boardId)
                    .orElse(0);
            response.put("hitCount", currentCount);

           return ResponseEntity.ok(response);

       } catch (Exception e) {
           log.error("Error toggling hit", e);
           response.put("error", e.getMessage());
           return ResponseEntity.internalServerError().body(response);
       }
   }

   // 좋아요 수 조회
   @GetMapping("/count/{boardId}")
   public ResponseEntity<Integer> getHitCount(@PathVariable int boardId) {
       try {
           int count = hitService.getHitCount(boardId)
                   .orElse(0);
           return ResponseEntity.ok(count);
       } catch (Exception e) {
           log.error("Error getting hit count", e);
           return ResponseEntity.internalServerError().build();
       }
   }

   // 좋아요 상태 확인
   @GetMapping("/status/{boardId}/{userId}")
   public ResponseEntity<Boolean> getHitStatus(
           @PathVariable int boardId,
           @PathVariable int userId) {

       try {
           boolean isHit = hitService.isHitByUser(boardId, userId)
                   .orElse(false);
           return ResponseEntity.ok(isHit);
       } catch (Exception e) {
           log.error("Error checking hit status", e);
           return ResponseEntity.internalServerError().build();
       }
   }
}