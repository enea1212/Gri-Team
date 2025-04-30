package com.example.demo.service;

import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.List;

@Component
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class); // Creăm logger-ul

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String username, String password) {
        logger.info("Login attempt for user: {}", username);


        if (!StringUtils.hasText(username) || !StringUtils.hasText(password)) {
            logger.error("Username or password is empty.");
            throw new RuntimeException("Fields cannot be empty");
        }

        // Log pentru a verifica username-ul înainte de căutare
        logger.info("Searching for user with username: {}", username);

        User utilizator = userRepository.findByUsername(username)
                .orElseThrow(() -> {
                    logger.error("Invalid login attempt for username: {}", username);
                    return new RuntimeException("Invalid data");
                });

        if (!passwordEncoder.matches(password, utilizator.getPassword())) {
            logger.error("Incorrect password for username: {}", username);
            throw new RuntimeException("Invalid data");
        }

        logger.info("Login successful for user: {}", username);

        // Listă de roluri pentru utilizator
        List<String> authorities = List.of("ROLE_USER");

        // Generează token-ul cu rolurile
        String JWTToken = jwtService.generateToken(utilizator.getUsername(), authorities);

        // Log pentru a verifica token-ul generat
        logger.info("JWT Token generated for user: {}", JWTToken);

        return JWTToken;
    }


    public String register(String username, String password, String email) {
        logger.info("Registration attempt for user: {} with email: {}", username, email);

        if (!StringUtils.hasText(username) || !StringUtils.hasText(password) || !StringUtils.hasText(email)) {
            logger.error("Username, password, or email is empty.");
            throw new RuntimeException("Fields cannot be empty");
        }

        if (userRepository.existsByUsername(username)) {
            logger.error("Username {} already exists", username);
            throw new RuntimeException("Invalid data");
        }

        if (userRepository.existsByEmail(email)) {
            logger.error("Email {} already exists", email);
            throw new RuntimeException("Invalid data");
        }

        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        logger.info("User {} registered successfully.", username);

        // Listă de roluri pentru utilizator
        List<String> authorities = List.of("ROLE_USER");

        // Generează token-ul cu rolurile
        String token = jwtService.generateToken(user.getUsername(), authorities);

        logger.info("JWT Token generated for registered user: {}", username);

        return token;
    }
}
