package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.model.ChargeRequest;
import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.ShoppingCart;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;
import mk.ukim.finki.eshop.model.enumeration.OrderStatus;
import mk.ukim.finki.eshop.service.AuthService;
import mk.ukim.finki.eshop.service.CategoryService;
import mk.ukim.finki.eshop.service.OrderService;
import mk.ukim.finki.eshop.service.ShoppingCartService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class CheckoutController {
    private final String stripePublicKey = "pk_test_51GtaiIC20OSy2aehzeih7APgwNObHZA7JVX5kwEkhq8iIdY2AmIJX07ic7yRY8kUERSJ4MzM3H3Ej5QokIhF2Ifm00FlCr2z1x";

    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private final OrderService orderService;

    public CheckoutController(CategoryService categoryService,
                              ShoppingCartService shoppingCartService,
                              AuthService authService,
                              OrderService orderService) {
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.orderService = orderService;
    }

    @GetMapping("/checkout/{orderNumber}")
    public String checkout(@PathVariable Long orderNumber, Model model) {
        ShoppingCart shoppingCart = this.shoppingCartService.findByUsernameAndStatus(this.authService.getCurrentUserId(), CartStatus.CREATED);
        Order order = this.orderService.findByOrderNumber(orderNumber);
        order.setOrderStatus(OrderStatus.PAYMENT_RECEIVED);
        this.orderService.save(order);
        model.addAttribute("size", shoppingCart.getProducts().size());
        model.addAttribute("ordersSize", this.orderService.findAllNewOrders().size());
        model.addAttribute("shoppingCart", shoppingCart);
        model.addAttribute("amount", (int) (shoppingCart.getCost() * 100));
        model.addAttribute("stripePublicKey", stripePublicKey);
        model.addAttribute("currency", "mkd");
        model.addAttribute("bodyContent", "checkout");
        return "master-details";
    }

    @PostMapping("/charge")
    public String checkout(ChargeRequest chargeRequest) {
        try {
            this.shoppingCartService.checkoutShoppingCart(this.authService.getCurrentUserId(), chargeRequest);
            return "redirect:/products?message=Successful Payment";
        } catch (RuntimeException ex) {
            return "redirect:/charge?error=" + ex.getLocalizedMessage();
        }
    }
}
