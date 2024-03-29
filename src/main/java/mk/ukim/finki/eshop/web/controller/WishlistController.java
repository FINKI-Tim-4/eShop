package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.Wishlist;
import mk.ukim.finki.eshop.model.exception.ProductIsAlreadyInWishlistException;
import mk.ukim.finki.eshop.service.AuthService;
import mk.ukim.finki.eshop.service.WishlistService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/wishlist")
public class WishlistController {

    private final WishlistService wishlistService;
    private final AuthService authService;

    public WishlistController(WishlistService wishlistService, AuthService authService) {
        this.wishlistService = wishlistService;
        this.authService = authService;
    }

    @GetMapping
    public String getWishlistPage(Model model) {
        if (this.authService.getCurrentUser() instanceof String) return "redirect:/login?error=Please, login first";
        Wishlist wishlist = this.wishlistService.findByUsername(this.authService.getCurrentUserId());
        List<Product> products = wishlist.getProductList();
        model.addAttribute("products", products);
        model.addAttribute("bodyContent", "wishlist");
        return "master-details";
    }

    @PostMapping("/add-product/{productId}")
    public String addProductToWishlist(@PathVariable Long productId) {
        if (this.authService.getCurrentUser() instanceof String) return "redirect:/login?error=Please, login first";
        String username = this.authService.getCurrentUserId();
        try {
            this.wishlistService.addProductToWishlist(username, productId);
            return "redirect:/products";
        } catch (ProductIsAlreadyInWishlistException ex) {
            return "redirect:/products?error=" + ex.getMessage();
        }
    }

    @PostMapping("/delete/{productId}")
    public String deleteProduct(@PathVariable Long productId) {
        this.wishlistService.deleteProduct(this.authService.getCurrentUserId(), productId);
        return "redirect:/wishlist";
    }
}
