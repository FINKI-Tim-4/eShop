package mk.ukim.finki.eshop.service;

import mk.ukim.finki.eshop.model.User;

public interface AuthService {
    User login(String username, String password);
    Object getCurrentUser();
    String getCurrentUserId();
}
