package com.example.demo.controller;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AuthService;
import com.example.demo.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Base64;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        // Verifică dacă există deja un utilizator cu același username

        String email = request.getEmail();
        String password = request.getPassword();
        String username = request.getUsername();
        try {
            String JWTToken = authService.register(username, password, email);
            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(JWTToken));
        } catch(RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        try {
            // În acest caz, loginRequest conține username și password din body
            String JWTToken = authService.login(loginRequest.getUsername(), loginRequest.getPassword());
            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(JWTToken));
        } catch (RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verifyEmail(@RequestParam("token") String token) {
        Optional<User> userOpt = userRepository.findByVerificationToken(token);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setEnabled(true);
            user.setVerificationToken(null); // Anulează tokenul după verificare
            userRepository.save(user);
            return ResponseEntity.ok("Contul a fost activat cu succes!");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Token invalid.");
        }
    }
}