package mk.ukim.finki.eshop.repository;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    List<Order> findAllByOrderStatus(OrderStatus status);

    Order findByOrderNumberIs(Long number);

    void removeByOrderNumber(Long number);

    List<Order> findAllByUsername(String username);
}
