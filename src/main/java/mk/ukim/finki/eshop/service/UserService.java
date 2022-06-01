package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.enumeration.Role;
import mk.ukim.finki.eshop.model.User;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

public interface UserService extends UserDetailsService {

    User register(String username, String password,
                  String name, String surname, Role role);

    List<Order> findAllByUsername(String username);

    List<Order> findAllCompletedOrders(String username);

    List<Order> findAllActiveOrders(String username);
}
