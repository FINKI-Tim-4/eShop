package mk.ukim.finki.eshop.service;

import com.stripe.exception.*;
import com.stripe.model.Charge;
import mk.ukim.finki.eshop.model.ChargeRequest;

public interface PaymentService {
    Charge pay(ChargeRequest chargeRequest) throws CardException,
            APIException,
            AuthenticationException,
            InvalidRequestException,
            APIConnectionException;

}
