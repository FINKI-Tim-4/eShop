package mk.ukim.finki.eshop.repository;

import mk.ukim.finki.eshop.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {
    public List<Category> findAllBySuperCategoryName(String name);

}
