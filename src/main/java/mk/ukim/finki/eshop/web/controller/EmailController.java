package mk.ukim.finki.eshop.web.controller;

import mk.ukim.finki.eshop.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class EmailController {

    private final EmailService emailService;

    public EmailController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public String sendEmail(@RequestParam String email){
        this.emailService.subscribe(email);
        this.emailService.sendSimpleMessage(email, "Subscription", "You are successfully subscribed to our mail service");
        return "redirect:/products";
    }
}
