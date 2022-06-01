package mk.ukim.finki.eshop.model;

import lombok.Data;

@Data
public class ChargeRequest {

    private int amount;
    private String currency;
    private String stripeToken;
}
