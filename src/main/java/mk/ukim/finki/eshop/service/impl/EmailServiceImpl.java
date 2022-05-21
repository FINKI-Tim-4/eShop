package mk.ukim.finki.eshop.service.impl;

import com.sun.istack.ByteArrayDataSource;
import mk.ukim.finki.eshop.model.Email;
import mk.ukim.finki.eshop.model.Order;
import mk.ukim.finki.eshop.model.exception.BadEmailFormat;
import mk.ukim.finki.eshop.repository.EmailRepository;
import mk.ukim.finki.eshop.service.EmailService;
import mk.ukim.finki.eshop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Service;

import javax.activation.DataHandler;
import javax.mail.Message;
import javax.mail.Multipart;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.util.List;

@Service
public class EmailServiceImpl implements EmailService {

    private final EmailRepository emailRepository;
    private final OrderService orderService;

    @Autowired
    private JavaMailSender emailSender;

    public EmailServiceImpl(EmailRepository emailRepository, OrderService orderService) {
        this.emailRepository = emailRepository;
        this.orderService = orderService;
    }

    public Email subscribe(String email){
        if(email == null || email.isEmpty()){
            throw new BadEmailFormat();
        }
        return emailRepository.save(new Email(email, true));
    }

    @Override
    public void sendMessageWithAttachment(String to, String subject, String text, Long orderNumber) {
//
        MimeMessagePreparator preparator = new MimeMessagePreparator()
        {
            public void prepare(MimeMessage mimeMessage) throws Exception
            {
                mimeMessage.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
                mimeMessage.setFrom("uiktp@finki.ukim.mk");
                mimeMessage.setSubject(subject);
                mimeMessage.setText(text);
                Order order = orderService.findByOrderNumber(orderNumber);

                Multipart multipart = new MimeMultipart();
                MimeBodyPart att = new MimeBodyPart();
                ByteArrayDataSource bds = new ByteArrayDataSource(order.getPdf(), "application/pdf");
                att.setDataHandler(new DataHandler(bds));
                att.setFileName("file.pdf");
                multipart.addBodyPart(att);
                mimeMessage.setContent(multipart);
            }
        };

        try {
            emailSender.send(preparator);
        }
        catch (MailException ex) {
            // simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }

    public void sendSimpleMessage(String to, String subject, String text){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("eshop@finki.ukim.mk");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }

    @Override
    public void notifyAllEmails() {
        List<Email> emails = emailRepository.findAll();
        emails.forEach(email -> sendSimpleMessage(email.getEmail(), "New Product", "We have a new product on our website, you can check it if you like"));
    }


}
