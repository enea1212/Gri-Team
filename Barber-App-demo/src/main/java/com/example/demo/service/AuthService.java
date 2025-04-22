package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;


@Component
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public String login(String username, String password) {

        User utilizator = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("Invalid data"));

        if (!passwordEncoder.matches(password, utilizator.getPassword())) {
            throw new RuntimeException("Invalid data");
        }

        String JWTToken = jwtService.generateToken(utilizator.getUsername());

        return JWTToken;
    }

    public String register(String username, String password, String email) {

        // Verifică dacă există deja un utilizator cu același username
        if (userRepository.existsByUsername(username)) {
            throw new RuntimeException("invalid data");
        }

        // Verifică dacă există deja un utilizator cu același email
        if (userRepository.existsByEmail(email)) {
            throw new RuntimeException("invalid data");
        }

        // Dacă nu există duplicate, creează un nou utilizator
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password)); // Asigură-te că parola este criptată
        userRepository.save(user);

        // Generează un token JWT
        String token = jwtService.generateToken(user.getUsername());
        return token;
    }
}
