package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;

import java.util.List;

public interface OrderService {
    Order save(Order order);
    Order findByOrderNumber(Long number);
    List<Order> findByUsername(String currentUserId);
    List<Order> findAllByStatus(OrderStatus status);
    List<Order> findAll();
    List<Order> findAllNewOrders();
    void cancelOrder(Long number);



}
