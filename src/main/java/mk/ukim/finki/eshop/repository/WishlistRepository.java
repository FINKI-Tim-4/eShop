package mk.ukim.finki.eshop.repository;

import mk.ukim.finki.eshop.model.Wishlist;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface WishlistRepository extends JpaRepository<Wishlist, Long> {
    Optional<Wishlist> findFirstByUsername(String username);
}
