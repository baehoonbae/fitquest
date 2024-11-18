package com.web.fitquest.service.category;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.web.fitquest.mapper.category.CategoryMapper;
import com.web.fitquest.model.category.Category;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    @Autowired
    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public boolean addCategory(Category category) {
        return categoryMapper.addCategory(category) > 0;
    }
}
