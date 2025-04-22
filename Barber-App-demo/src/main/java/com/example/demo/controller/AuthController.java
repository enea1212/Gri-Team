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

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

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
    public ResponseEntity<?> login(@RequestHeader("Authorization") String token) {
        String user;
        String pass;

        token = token.split(" ")[1];
        String decodedToken = new String(Base64.getDecoder().decode(token));

        String[] decodedTokenSplit = decodedToken.split(":");
        user = decodedTokenSplit[0];
        pass = decodedTokenSplit[1];
        try {
            String JWTToken = authService.login(user, pass);
            return ResponseEntity.status(HttpStatus.OK).body(new AuthResponse(JWTToken));
        } catch(RuntimeException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }
}
