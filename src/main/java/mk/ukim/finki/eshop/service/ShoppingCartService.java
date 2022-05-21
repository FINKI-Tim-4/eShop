package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.ChargeRequest;
import mk.ukim.finki.eshop.model.ShoppingCart;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;

import java.util.List;

public interface ShoppingCartService {
   List<ShoppingCart> findAll();
   List<ShoppingCart> findByUsername(String username);
   ShoppingCart addProductToShoppingCart(String username, Long id);
   ShoppingCart findByUsernameAndStatus(String username, CartStatus status);
   ShoppingCart save(ShoppingCart shoppingCart);
   void checkoutShoppingCart(String userId, ChargeRequest chargeRequest);
   void deleteById(Long id);

}
