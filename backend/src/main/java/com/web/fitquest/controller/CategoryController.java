package com.web.fitquest.controller;

import com.web.fitquest.model.category.Category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.web.fitquest.service.category.CategoryService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/category")
@Slf4j
public class CategoryController {

    private CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // user의 id에 해당하는 모든 카테고리 반환
    @GetMapping("/list/{userId}")
    public ResponseEntity<?> getCategoryListByUserId(@PathVariable int userId) {
        try {
            // 구현부 입니다.

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // category의 id에 해당하는 카테고리 반환
    @GetMapping("/{categoryId}")
    public ResponseEntity<?> getCategoryByCategoryId(@PathVariable int categoryId) {
        try {
            // 구현부 입니다.

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // category 추가
    @PostMapping("/add")
    public ResponseEntity<?> addCategory(@RequestBody Category category) {
        try {
            boolean success =  categoryService.addCategory(category);
            if(success) {
                return new ResponseEntity<>("카테고리 추가 성공", HttpStatus.OK);
            } else {
                return new ResponseEntity<>("카테고리 추가 실패", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("서버 오류", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // category의 id에 해당하는 카테고리 수정
    @PutMapping("/{categoryId}")
    public ResponseEntity<?> updateCategoryByCategoryId(@PathVariable int categoryId, @RequestBody Category category) {
        try {
            // 구현부 입니다.

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // category의 id에 해당하는 카테고리 삭제
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<?> deleteCategoryByCategoryId(@PathVariable int categoryId) {
        try {
            // 구현부 입니다.

            return new ResponseEntity<>(HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
