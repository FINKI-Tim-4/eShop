package mk.ukim.finki.eshop.model;

import com.sun.istack.NotNull;
import lombok.Data;
import mk.ukim.finki.eshop.model.enumeration.Size;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "image")
    @Lob
    private String base64Image;

    @NotNull
    private String name;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private Size size;

    private LocalDateTime createAt;

    private boolean stock;

    @NotNull
    private Float price;

    @ManyToOne
    @NotNull
    private Category category;

    private String description;

    public Product() {
    }

    public Product(String name, Size size, Float price, Category category, String description){
        this.name = name;
        this.size = size;
        this.price = price;
        this.category = category;
        this.description = description;
        this.stock=true;
        this.createAt = LocalDateTime.now();
    }

    public Product(String name, Size size, Float price, Category category, String description, String base64Image){
        this.name = name;
        this.size = size;
        this.price = price;
        this.category = category;
        this.description = description;
        this.base64Image = base64Image;
        this.stock=true;
        this.createAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", name='" + name +
                ", size=" + size +
                ", price=" + price +
                ", category=" + category +
                '}';
    }
}