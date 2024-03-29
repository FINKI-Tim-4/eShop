package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.ShoppingCart;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;
import mk.ukim.finki.eshop.service.AuthService;
import mk.ukim.finki.eshop.service.EmailService;
import mk.ukim.finki.eshop.service.OrderService;
import mk.ukim.finki.eshop.service.ShoppingCartService;
import mk.ukim.finki.eshop.util.GeneratePdf;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;

@Controller
public class OrderController {

    private final ShoppingCartService shoppingCartService;
    private final OrderService orderService;
    private final EmailService emailService;
    private final AuthService authService;

    public OrderController(ShoppingCartService shoppingCartService, OrderService orderService, EmailService emailService, AuthService authService) {
        this.shoppingCartService = shoppingCartService;
        this.orderService = orderService;
        this.emailService = emailService;
        this.authService = authService;
    }

    @GetMapping("/make-order")
    public String submitOrder(Model model) {
        ShoppingCart shoppingCart = this.shoppingCartService
                .findByUsernameAndStatus(this.authService.getCurrentUserId(), CartStatus.CREATED);
        //test
        model.addAttribute("size", shoppingCart.getProducts().size());
        model.addAttribute("ordersSize", this.orderService.findAllNewOrders().size());
        //test
        model.addAttribute("products", shoppingCart.getProducts());
        model.addAttribute("bodyContent", "submit-order");
        return "master-details";
    }

    @PostMapping("/submit-order")
    private String makeOrder(@RequestParam String name,
                             @RequestParam String surname,
                             @RequestParam String address,
                             @RequestParam String email,
                             @RequestParam String phoneNumber,
                             @RequestParam String payType) {
        ShoppingCart shoppingCart = this.shoppingCartService
                .findByUsernameAndStatus(this.authService.getCurrentUserId(), CartStatus.CREATED);
        Order order = new Order(this.authService.getCurrentUserId(),
                name, surname, address, email, phoneNumber);
        order.setProducts(shoppingCart.getProducts());
        order.setTotal(order.getProducts().stream().mapToDouble(Product::getPrice).sum());
        shoppingCart.setProducts(new ArrayList<>());

        GeneratePdf generatePdf = new GeneratePdf(orderService);
        orderService.save(order);
        generatePdf.orderReport(order);
        emailService.sendMessageWithAttachment(order.getEmail(), "TEST", "test", order.getOrderNumber());
        if (payType.equals("card")) {
            Long number = order.getOrderNumber();
            return "redirect:/checkout/" + number;
        } else return "redirect:/user/active-orders";
    }

    @PostMapping("/confirm-order/{number}")
    public String confirmOrder(@PathVariable Long number) {
        Order order = this.orderService.findByOrderNumber(number);
        order.setOrderStatus(OrderStatus.DELIVERY_ON_PROCESS);
        this.orderService.save(order);
        return "redirect:/admin/new-orders";
    }

    @PostMapping("/completed-order/{number}")
    public String completedOrder(@PathVariable Long number) {
        Order order = this.orderService.findByOrderNumber(number);
        order.setOrderStatus(OrderStatus.COMPLETED);
        this.orderService.save(order);
        return "redirect:/admin/confirmed-orders";
    }

    @PostMapping("/cancel-order/{number}")
    public String cancelOrder(@PathVariable Long number) {
        this.orderService.cancelOrder(number);
        return "redirect:/admin/new-orders";
    }

    @PostMapping("/shopping-cart/cancel-order/{number}")
    public String cancelOrderUser(@PathVariable Long number) {
        this.orderService.cancelOrder(number);
        return "redirect:/user/all-orders";
    }
}
