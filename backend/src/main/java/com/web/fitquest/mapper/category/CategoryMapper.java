package com.web.fitquest.mapper.category;

import org.apache.ibatis.annotations.Mapper;

import com.web.fitquest.model.category.Category;

@Mapper
public interface CategoryMapper {
    int addCategory(Category category);
}
