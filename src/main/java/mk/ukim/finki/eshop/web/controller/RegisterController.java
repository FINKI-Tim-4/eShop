package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.config.CustomUsernamePasswordAuthenticationProvider;
import mk.ukim.finki.eshop.model.enumeration.Role;
import mk.ukim.finki.eshop.model.exception.InvalidArgumentsException;
import mk.ukim.finki.eshop.model.exception.PasswordDoNotMatchException;
import mk.ukim.finki.eshop.model.exception.UserNameExistsException;
import mk.ukim.finki.eshop.service.AuthService;

import mk.ukim.finki.eshop.service.CategoryService;

import mk.ukim.finki.eshop.service.ShoppingCartService;
import mk.ukim.finki.eshop.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/register")
public class RegisterController {

    private final CategoryService categoryService;
    private final UserService userService;
    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private final CustomUsernamePasswordAuthenticationProvider provider;

    public RegisterController(CategoryService categoryService,
                              UserService userService,
                              ShoppingCartService shoppingCartService,
                              AuthService authService,
                              CustomUsernamePasswordAuthenticationProvider provider) {
        this.categoryService = categoryService;
        this.userService = userService;
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.provider = provider;

    }

    @PostMapping
    public String register(@RequestParam String username,
                           @RequestParam String password,
                           @RequestParam String name,
                           @RequestParam String surname,
//                           @RequestParam Role role,
                           Model model) {
        try {
            this.userService.register(username, password, name, surname, Role.ROLE_USER);
        } catch (PasswordDoNotMatchException | InvalidArgumentsException | UserNameExistsException exception) {
            return "redirect:/register?error=" + exception.getMessage();
        }

        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("tops", this.categoryService.findAllBySuperCategoryName("TOP"));
        model.addAttribute("bottoms", this.categoryService.findAllBySuperCategoryName("BOTTOM"));
        model.addAttribute("accessories", this.categoryService.findAllBySuperCategoryName("ACCESSORIES"));
        model.addAttribute("collections", this.categoryService.findAllBySuperCategoryName("COLLECTIONS"));

//        if(this.authService.getCurrentUserId() != null) {
//            ShoppingCart shoppingCart = this.shoppingCartService.findByUsernameAndStatus(this.authService.getCurrentUserId(), CartStatus.CREATED);
//            model.addAttribute("size", shoppingCart.getProducts().size());
//        }
//        else model.addAttribute("size", 0);
        return "redirect:/login";
    }

    @GetMapping
    public String getRegisterPage(@RequestParam(required = false) String error, Model model) {
        if (error != null && !error.isEmpty()) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }
        model.addAttribute("bodyContent", "register");
        return "master-details";
    }
}
