package mk.ukim.finki.eshop.repository;

import mk.ukim.finki.eshop.model.ShoppingCart;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Long> {
    ShoppingCart findFirstByUsername(String username);
    Optional<ShoppingCart> findByUsernameAndStatus(String username, CartStatus status);
}
