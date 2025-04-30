package com.example.demo.controller;

import com.example.demo.dto.AnuntDTO;
import com.example.demo.entity.User;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AnuntService;
import jakarta.validation.constraints.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/anunturi")
@Validated
public class AnuntController {

    private final AnuntService anuntService;
    private final UserRepository userRepository;

    public AnuntController(AnuntService anuntService, UserRepository userRepository) {
        this.anuntService = anuntService;
        this.userRepository = userRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createAnunt(
            @RequestParam @NotBlank(message = "Titlul este obligatoriu") String titlu,
            @RequestParam @NotBlank(message = "Descrierea este obligatorie") String descriere,
            @RequestParam @NotNull(message = "Prețul este obligatoriu")
            @Positive(message = "Prețul trebuie să fie pozitiv") Double pret,
            @RequestParam @NotBlank(message = "Locația este obligatorie") String locatie,
            @RequestParam(required = false) MultipartFile poza,
            Authentication authentication) {

        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul nu a fost găsit"));

            AnuntDTO createdAnunt = anuntService.createAnunt(titlu, descriere, pret, locatie, poza, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdAnunt);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Neautorizat", "message", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body(Map.of("error", "Eroare la crearea anunțului",
                            "details", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnunt(
            @PathVariable Long id,
            Authentication authentication) {

        try {
            User user = userRepository.findByUsername(authentication.getName())
                    .orElseThrow(() -> new UsernameNotFoundException("Utilizatorul nu a fost găsit"));

            return anuntService.deleteAnunt(id, user);

        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Neautorizat: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("Eroare la ștergerea anunțului: " + e.getMessage());
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<AnuntDTO>> getAllAnunturiExceptMine(Authentication authentication) {
        User currentUser = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        List<AnuntDTO> anunturi = anuntService.getAllAnunturiExceptCurrentUser(currentUser.getId());
        return ResponseEntity.ok(anunturi);
    }
}