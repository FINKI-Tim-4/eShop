package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.model.User;
import mk.ukim.finki.eshop.model.exception.InvalidUserCredentialsException;
import mk.ukim.finki.eshop.service.AuthService;

import mk.ukim.finki.eshop.service.CategoryService;

import mk.ukim.finki.eshop.service.OrderService;
import mk.ukim.finki.eshop.service.ShoppingCartService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/login")
public class LoginController {
    private final PasswordEncoder passwordEncoder;
    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private final CategoryService categoryService;
    private final OrderService orderService;

    public LoginController(PasswordEncoder passwordEncoder,
                           ShoppingCartService shoppingCartService,
                           AuthService authService,
                           CategoryService categoryService, OrderService orderService) {
        this.passwordEncoder = passwordEncoder;
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.categoryService = categoryService;
        this.orderService = orderService;
    }

    @GetMapping
    public String getLoginPage(Model model, @RequestParam(required = false) String error) {
        if (error != null) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        model.addAttribute("bodyContent", "login");

        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("tops", this.categoryService.findAllBySuperCategoryName("TOP"));
        model.addAttribute("bottoms", this.categoryService.findAllBySuperCategoryName("BOTTOM"));
        model.addAttribute("accessories", this.categoryService.findAllBySuperCategoryName("ACCESSORIES"));
        model.addAttribute("collections", this.categoryService.findAllBySuperCategoryName("COLLECTIONS"));

        return "master-details";
    }

    @PostMapping
    public String login(HttpServletRequest request, Model model) {
        User user;
        try {
            user = this.authService.login(request.getParameter("username")
                    , request.getParameter("password"));
            request.getSession().setAttribute("user", user);
            return "redirect:/products";
        } catch (InvalidUserCredentialsException exception) {
            model.addAttribute("error", exception.getMessage());
            model.addAttribute("bodyContent", "login");
            return "master-details";
        }
    }
}
