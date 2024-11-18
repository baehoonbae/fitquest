package com.web.fitquest.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.model.category.Category;
import com.web.fitquest.service.category.CategoryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
@Slf4j
public class CategoryController {

    private final CategoryService categoryService;

    // 카테고리 목록 조회
    @GetMapping("/{userId}")
    public ResponseEntity<?> getCategoryList(@PathVariable int userId) {
        try {
            Optional<List<Category>> opCategoryList = categoryService.getCategoryList(userId);
            return opCategoryList.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // 특정 카테고리 조회
    @GetMapping("/{userId}/{categoryId}")
    public ResponseEntity<?> getCategory(@PathVariable int userId, @PathVariable int categoryId) {
        try {
            Optional<Category> opCategory = categoryService.getCategoryByUserIdAndCategoryId(userId, categoryId);
            return opCategory.map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("서버 오류 발생");
        }
    }

    // 카테고리 삭제
    // @DeleteMapping("/{categoryId}")
    // public ResponseEntity<?> deleteCategory(@PathVariable int categoryId) {
    //     try {
    //         boolean success = categoryService.deleteCategory(categoryId);
    //         return success 
    //             ? ResponseEntity.ok("카테고리 삭제 성공")
    //             : ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("카테고리 삭제 실패");
    //     } catch (Exception e) {
    //         e.printStackTrace();
    //         return ResponseEntity.internalServerError().body("서버 오류 발생");
    //     }
    // }
}
