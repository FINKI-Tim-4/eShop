package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.Wishlist;
import mk.ukim.finki.eshop.repository.WishlistRepository;
import mk.ukim.finki.eshop.service.ProductService;
import mk.ukim.finki.eshop.service.WishlistService;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Service
public class WishlistServiceImpl implements WishlistService {

    private final WishlistRepository repository;
    private final ProductService productService;

    public WishlistServiceImpl(WishlistRepository repository, ProductService productService) {
        this.repository = repository;
        this.productService = productService;
    }

    @Override
    @Transactional
    public Wishlist findByUsername(String username) {
        return this.repository.findFirstByUsername(username)
                .orElseGet(()-> {
                    Wishlist wishlist = new Wishlist(username);
                    return this.repository.save(wishlist);
                });
    }

    @Override
    @Transactional
    public Wishlist addProductToWishlist(String username, Long productId) {
        Wishlist wishlist = this.findByUsername(username);
        if(wishlist.getProductList().stream().noneMatch(product -> product.getId() == productId))
            wishlist.getProductList().add(this.productService.findById(productId));
        return this.repository.save(wishlist);
    }

    @Override
    @Transactional
    public List<Product> getProductsInWishlist(String username) {
        return this.findByUsername(username).getProductList();
    }

    @Override
    @Transactional
    public Wishlist deleteProduct(String username, Long productId) {
        Wishlist wishlist = this.findByUsername(username);
        wishlist.getProductList().removeIf(product -> product.getId()==productId);
        return this.repository.save(wishlist);
    }


}
