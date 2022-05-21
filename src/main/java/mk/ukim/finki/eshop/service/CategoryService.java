package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.Category;

import java.util.List;

public interface CategoryService {
    List<Category> findAll();
    void deleteByName(String name);
    Category saveCategory(String name, Category superCategory);
    List<Category> findAllBySuperCategoryName(String name);
}
