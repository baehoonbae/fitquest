package com.web.fitquest.mapper.category;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.web.fitquest.model.category.Category;

@Mapper
public interface CategoryMapper {
    int addCategory(Category category);
    List<Category> getCategoryList(int userId);
}
