package mk.ukim.finki.eshop.service.impl;

import mk.ukim.finki.eshop.config.CustomUsernamePasswordAuthenticationProvider;
import mk.ukim.finki.eshop.model.User;
import mk.ukim.finki.eshop.model.exception.InvalidArgumentsException;
import mk.ukim.finki.eshop.model.exception.InvalidUserCredentialsException;
import mk.ukim.finki.eshop.repository.UserRepository;
import mk.ukim.finki.eshop.service.AuthService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;

    public AuthServiceImpl(UserRepository userRepository, PasswordEncoder passwordEncoder, CustomUsernamePasswordAuthenticationProvider customUsernamePasswordAuthenticationProvider) {
        this.userRepository = userRepository;

    }

    @Override
    public User login(String username, String password) {
        if (username==null || username.isEmpty() || password==null || password.isEmpty()) {
            throw new InvalidArgumentsException();
        }

        return this.userRepository.findByUsernameAndPassword(username,
                password)
                .orElseThrow(InvalidUserCredentialsException::new);
    }

    @Override
    public Object getCurrentUser() {
        return SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }

    @Override
    public String getCurrentUserId() {
        User user = (User) this.getCurrentUser();
        return user.getUsername();
    }
}
