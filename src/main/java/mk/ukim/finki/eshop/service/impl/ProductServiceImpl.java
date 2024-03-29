package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.model.Category;
import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.enumeration.Size;
import mk.ukim.finki.eshop.model.exception.BadArgumentsException;
import mk.ukim.finki.eshop.model.exception.CategoryNotFoundException;
import mk.ukim.finki.eshop.model.exception.ProductNotFoundException;
import mk.ukim.finki.eshop.repository.CategoryRepository;
import mk.ukim.finki.eshop.repository.ProductRepository;
import mk.ukim.finki.eshop.service.ProductService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Base64;
import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public ProductServiceImpl(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<Product> findAll() {
        return this.productRepository.findAll();
    }

    @Transactional
    @Override
    public Page<Product> findAll(Pageable pageable) {
        return this.productRepository.findAllByStock(pageable, true);
    }

    @Override
    public Product findById(Long id) {
        return this.productRepository.findById(id).orElse(null);
    }

    @Transactional
    @Override
    public List<Product> findAllBySuperCategory(String category) {
        Category superCategory = this.categoryRepository.findById(category).orElseThrow(CategoryNotFoundException::new);
        return this.productRepository.findAllByCategory_SuperCategory(superCategory);
    }

    @Transactional
    @Override
    public Page<Product> findAllBySuperCategory(String category, Pageable pageable) {
        Category superCategory = this.categoryRepository.findById(category).orElseThrow(CategoryNotFoundException::new);
        return this.productRepository.findAllByCategory_SuperCategoryAndStock(superCategory, pageable, true);
    }

    @Transactional
    @Override
    public List<Product> findAllByCategory(String category) {
        Category cat = this.categoryRepository.findById(category).orElseThrow(CategoryNotFoundException::new);
        return this.productRepository.findAllByCategoryAndStock(cat, true);
    }

    @Transactional
    @Override
    public Page<Product> findAllByCategory(String category, Pageable pageable) {
        Category cat = this.categoryRepository.findById(category).orElseThrow(CategoryNotFoundException::new);
        return this.productRepository.findAllByCategoryAndStock(cat, pageable, true);
    }

    @Override
    public Product save(String name, Size size, float price, String cat, String description, MultipartFile image) throws IOException {
        if(name==null || name.isEmpty())
            throw new BadArgumentsException();
        Category category = this.categoryRepository.findById(cat).orElseThrow(BadArgumentsException::new);
        byte[] bytes = image.getBytes();
        String base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(bytes));
        return  this.productRepository.save(new Product(name,size, price, category, description, base64Image));
    }

    @Override
    public Product editProduct(Long id, String name, Size size, float price, String cat, String description, MultipartFile image) throws IOException {
        Product product = this.productRepository.findById(id).orElseThrow(()->new ProductNotFoundException(id));
        if(name==null || name.isEmpty())
            throw new BadArgumentsException();
        Category category = this.categoryRepository.findById(cat).orElseThrow(BadArgumentsException::new);
        product.setName(name);
        product.setSize(size);
        product.setPrice(price);
        product.setCategory(category);
        product.setDescription(description);
        String base64Image;
        if(!image.isEmpty()){
            byte[] bytes = image.getBytes();
            base64Image = String.format("data:%s;base64,%s", image.getContentType(), Base64.getEncoder().encodeToString(bytes));
        }
        else
            base64Image = product.getBase64Image();
        product.setBase64Image(base64Image);
        return this.productRepository.save(product);
    }

    @Override
    public void deleteById(Long id) {
        this.productRepository.deleteById(id);
    }

    @Override
    public Product save(Product product) {
        return this.productRepository.save(product);
    }
}
