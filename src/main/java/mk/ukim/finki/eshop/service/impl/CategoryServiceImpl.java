package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.model.Category;
import mk.ukim.finki.eshop.repository.CategoryRepository;
import mk.ukim.finki.eshop.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Category> findAll() {
        return this.categoryRepository.findAll();
    }

    @Override
    public void deleteByName(String name) {
        this.categoryRepository.deleteById(name);
    }

    @Override
    public Category saveCategory(String name, Category superCategory) {
        Category category = this.categoryRepository.findById(name).orElseGet(()->null);
        if(category!=null){
            category.setName(name);
            return this.categoryRepository.save(category);
        }
        return this.categoryRepository.save(new Category(name, superCategory));
    }

    @Override
    public List<Category> findAllBySuperCategoryName(String name) {
        return this.categoryRepository.findAllBySuperCategoryName(name);
    }


}
