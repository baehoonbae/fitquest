package com.web.fitquest.service.category;

import java.util.List;
import java.util.Optional;

import com.web.fitquest.model.category.Category;

public interface CategoryService {
    Optional<List<Category>> getCategoryList(int userId);
    boolean addCategory(Category category);
    Optional<Category> getCategoryByUserIdAndCategoryId(int userId, int categoryId);
}
