package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;
import mk.ukim.finki.eshop.model.enumeration.Role;
import mk.ukim.finki.eshop.model.User;
import mk.ukim.finki.eshop.model.exception.InvalidUsernameOrPasswordException;
import mk.ukim.finki.eshop.model.exception.UserNameExistsException;
import mk.ukim.finki.eshop.repository.OrderRepository;
import mk.ukim.finki.eshop.repository.UserRepository;
import mk.ukim.finki.eshop.service.UserService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OrderRepository orderRepository;


    public UserServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, OrderRepository orderRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.orderRepository = orderRepository;
    }

    @Override
    public User register(String username, String password,
                         String name, String surname, Role role) {
        if(username==null || username.isEmpty() || password==null || password.isEmpty())
            throw new InvalidUsernameOrPasswordException();
        if(this.userRepository.findByUsername(username).isPresent())
            throw new UserNameExistsException(username);
        User user = new User(username, passwordEncoder.encode(password), name, surname, role);
        return userRepository.save(user);
    }

    @Override
    @Transactional
    public List<Order> findAllByUsername(String username) {
        return this.orderRepository.findAllByUsername(username)
                .stream()
                .sorted(Comparator.comparing(Order::getCreateTime).reversed())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Order> findAllCompletedOrders(String username) {
        return this.findAllByUsername(username)
                .stream()
                .filter(order -> order.getOrderStatus()==OrderStatus.COMPLETED)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public List<Order> findAllActiveOrders(String username) {
        return this.orderRepository.findAllByUsername(username)
                .stream()
                .filter(order -> order.getOrderStatus()==OrderStatus.PENDING
                        || order.getOrderStatus()==OrderStatus.PAYMENT_RECEIVED
                        || order.getOrderStatus()==OrderStatus.DELIVERY_ON_PROCESS)
                .collect(Collectors.toList());
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        return userRepository.findByUsername(s).orElseThrow(()-> new UsernameNotFoundException(s));
    }
}