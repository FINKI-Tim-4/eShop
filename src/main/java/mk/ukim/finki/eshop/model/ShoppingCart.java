package mk.ukim.finki.eshop.model;

import lombok.Data;
import mk.ukim.finki.eshop.model.enumeration.CartStatus;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class ShoppingCart{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dateCreate;

    private String username;

    @ManyToMany(fetch = FetchType.EAGER)
    private List<Product> products;

    @Enumerated(EnumType.STRING)
    private CartStatus status;

    private double cost;

    public ShoppingCart(){}

    public ShoppingCart(String username) {
        this.dateCreate = LocalDateTime.now();
        this.username = username;
        this.status = CartStatus.CREATED;
        this.products = new ArrayList<>();
        this.cost = 0;
    }
}
