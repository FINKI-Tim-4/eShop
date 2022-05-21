package mk.ukim.finki.eshop.model;

import lombok.Data;

@Data
public class SortClass {
    String name;
    String value;

    public SortClass(String name, String value) {
        this.name = name;
        this.value = value;
    }
}
