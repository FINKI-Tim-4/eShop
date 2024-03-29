package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.model.Product;
import mk.ukim.finki.eshop.model.ShoppingCart;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;
import mk.ukim.finki.eshop.model.enumeration.Size;
import mk.ukim.finki.eshop.model.exception.ProductIsAlreadyInShoppingCartException;
import mk.ukim.finki.eshop.model.SortClass;
import mk.ukim.finki.eshop.service.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@RequestMapping("products")
public class ProductController {

    private final ProductService productService;
    private final CategoryService categoryService;
    private final ShoppingCartService shoppingCartService;
    private final AuthService authService;
    private final EmailService emailService;
    private final OrderService orderService;
    Pageable firstPage = PageRequest.of(0, 6);

    public ProductController(ProductService productService, CategoryService categoryService, ShoppingCartService shoppingCartService, AuthService authService, EmailService emailService, OrderService orderService) {
        this.productService = productService;
        this.categoryService = categoryService;
        this.shoppingCartService = shoppingCartService;
        this.authService = authService;
        this.emailService = emailService;
        this.orderService = orderService;
    }

    @GetMapping({"", "/{category}"})
    public String getProductsPage(@RequestParam(required = false) String error,
                                  @RequestParam(required = false) String sort,
                                  @PathVariable(required = false) String category,
                                  @RequestParam(required = false) String state,
                                  @RequestParam(required = false) Integer page,
                                  Model model) {
        if (error != null) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", error);
        }

        if (sort == null) {
            model.addAttribute("sorting", "created-descending");
            sort = "created-descending";
        } else
            model.addAttribute("sorting", sort);

        if (page == null) {
            page = 0;
        }
        model.addAttribute("page", page);

        List<Product> products = sorted(sort, category, page, model);

        model.addAttribute("state", state);

        addToModel(model, products);

        temelkovkiTEST(model);

        return "master-details";
    }

    private void temelkovkiTEST(Model model) {
        //test
        try {
            ShoppingCart shoppingCart = this.shoppingCartService.findByUsernameAndStatus(this.authService.getCurrentUserId(), CartStatus.CREATED);
            model.addAttribute("size", shoppingCart.getProducts().size());
        } catch (RuntimeException ex) {
            model.addAttribute("size", 0);

        }//test
    }

    @GetMapping("/add-product")
//    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String getAddProductPage(Model model) {
        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("sizes", Size.values());
        model.addAttribute("product", new Product());
        model.addAttribute("bodyContent", "add-product");
        return "master-details";
    }

    @GetMapping("/edit/{id}")
    public String editProductPage(@PathVariable Long id,
                                  Model model) {
        model.addAttribute("product", this.productService.findById(id));
        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("sizes", Size.values());
        model.addAttribute("bodyContent", "add-product");
        return "master-details";
    }

    @PostMapping("/product/{id}")
    public String addNewProduct(@PathVariable Long id,
                                @RequestParam String name,
                                @RequestParam(required = false) Size size,
                                @RequestParam Float price,
                                @RequestParam String category,
                                @RequestParam String description,
                                @RequestParam MultipartFile image) throws IOException {
        Product product = productService.findById(id);
        if (product != null)
            productService.editProduct(id, name, size, price, category, description, image);
        else {
            productService.save(name, size, price, category, description, image);
//            emailService.notifyAllEmails();
        }
        return "redirect:/products";
    }

    @PostMapping("/delete/{id}")
    public String deleteProductWithPost(@PathVariable Long id) {
        try {
            this.productService.deleteById(id);
        } catch (ProductIsAlreadyInShoppingCartException ex) {
            return String.format("redirect:/products?error=%s", ex.getMessage());
        }
        return "redirect:/products";
    }

    private void addToModel(Model model, List<Product> products) {
        model.addAttribute("sortClass", napolni());
        model.addAttribute("ordersSize", this.orderService.findAllNewOrders().size());
        model.addAttribute("products", products);
        model.addAttribute("bodyContent", "products");
        model.addAttribute("categories", this.categoryService.findAll());
        model.addAttribute("tops", this.categoryService.findAllBySuperCategoryName("TOP"));
        model.addAttribute("bottoms", this.categoryService.findAllBySuperCategoryName("BOTTOM"));
        model.addAttribute("accessories", this.categoryService.findAllBySuperCategoryName("ACCESSORIES"));
        model.addAttribute("collections", this.categoryService.findAllBySuperCategoryName("COLLECTIONS"));
    }

    private List<Product> sorted(String sort, String category, Integer pagee, Model model) {
        Pageable page = null;

        switch (sort) {
            case "price-ascending":
                page = PageRequest.of(pagee, 6, Sort.by("price"));
                break;
            case "price-descending":
                page = PageRequest.of(pagee, 6, Sort.by("price").descending());
                break;
            case "title-ascending":
                page = PageRequest.of(pagee, 6, Sort.by("name"));
                break;
            case "title-descending":
                page = PageRequest.of(pagee, 6, Sort.by("name").descending());
                break;
            case "created-descending":
                page = PageRequest.of(pagee, 6, Sort.by("createAt").descending());
                break;
            case "created-ascending":
                page = PageRequest.of(pagee, 6, Sort.by("createAt"));
        }

        if (category == null)
            category = "all";

        List<Product> lista = null;

        Page<Product> productPage;

        switch (category) {
            case "all":
                productPage = this.productService.findAll(page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "top":
                productPage = this.productService.findAllBySuperCategory("TOP", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "bottom":
                productPage = this.productService.findAllBySuperCategory("BOTTOM", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "accessories":
                productPage = this.productService.findAllBySuperCategory("ACCESSORIES", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "collections":
                productPage = this.productService.findAllBySuperCategory("COLLECTIONS", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Shirts":
                productPage = this.productService.findAllByCategory("Shirts", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Jackets":
                productPage = this.productService.findAllByCategory("Jackets", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Hoodies":
                productPage = this.productService.findAllByCategory("Hoodies", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "T-Shirts":
                productPage = this.productService.findAllByCategory("T-Shirts", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Knitwear":
                productPage = this.productService.findAllByCategory("Knitwear", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Blouses":
                productPage = this.productService.findAllByCategory("Blouses", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Tops":
                productPage = this.productService.findAllByCategory("Tops", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Trousers":
                productPage = this.productService.findAllByCategory("Trousers", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Jeans":
                productPage = this.productService.findAllByCategory("Jeans", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Skirts":
                productPage = this.productService.findAllByCategory("Skirts", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Earrings":
                productPage = this.productService.findAllByCategory("Earrings", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Bags":
                productPage = this.productService.findAllByCategory("Bags", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
            case "Dresses":
                productPage = this.productService.findAllByCategory("Dresses", page);
                model.addAttribute("pages", productPage.getTotalPages());
                return productPage.get().collect(Collectors.toList());
        }

        return lista;
    }

    private List<SortClass> napolni() {
        List<SortClass> lista = new ArrayList<>();
        lista.add(new SortClass("Lowest Price", "price-ascending"));
        lista.add(new SortClass("Highest Price", "price-descending"));
        lista.add(new SortClass("Alphabetically, A-Z", "title-ascending"));
        lista.add(new SortClass("Alphabetically, Z-A", "title-descending"));
        lista.add(new SortClass("Date, New to Old", "created-descending"));
        lista.add(new SortClass("Date, Old to New", "created-ascending"));
        return lista;
    }
}
