package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.Wishlist;

import java.util.List;

public interface WishlistService {
    Wishlist findByUsername(String username);
    Wishlist addProductToWishlist(String username, Long productId);
    List<Product> getProductsInWishlist(String username);
    Wishlist deleteProduct(String username, Long productId);
}
