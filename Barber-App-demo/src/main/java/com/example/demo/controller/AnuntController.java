package com.example.demo.controller;

import com.example.demo.dto.*;
import com.example.demo.entity.Anunt;
import com.example.demo.entity.User;
import com.example.demo.repository.AnuntRepository;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.AnuntService;
import jakarta.validation.constraints.*;
import org.springframework.http.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.*;

@RestController
@RequestMapping("/api/anunturi")
@Validated
public class AnuntController {
    private final AnuntService anuntService;
    private final UserRepository userRepository;
    private final AnuntRepository anuntRepository;

    public AnuntController(AnuntService anuntService, UserRepository userRepository, AnuntRepository anuntRepository) {
        this.anuntService = anuntService;
        this.userRepository = userRepository;
        this.anuntRepository = anuntRepository;
    }

    @PostMapping(consumes = "multipart/form-data")
    public ResponseEntity<?> createAnunt(
            @RequestParam @NotBlank String titlu,
            @RequestParam @NotBlank String descriere,
            @RequestParam @NotNull @Positive Double pret,
            @RequestParam @NotBlank String locatie,
            @RequestParam(required = false) MultipartFile poza,
            Authentication auth) {
        try {
            User user = getUserFromAuth(auth);
            AnuntDTO anunt = anuntService.createAnunt(titlu, descriere, pret, locatie, poza, user);
            return ResponseEntity.status(HttpStatus.CREATED).body(anunt);
        } catch (UsernameNotFoundException e) {
            return unauthorizedResponse(e);
        } catch (Exception e) {
            return errorResponse("Eroare la creare anunț", e);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAnunt(@PathVariable Long id, Authentication auth) {
        try {
            User user = getUserFromAuth(auth);
            return anuntService.deleteAnunt(id, user);
        } catch (UsernameNotFoundException e) {
            return unauthorizedResponse(e);
        } catch (Exception e) {
            return errorResponse("Eroare la ștergere", e);
        }
    }

    @GetMapping("/public")
    public ResponseEntity<List<AnuntDTO>> getAnunturiPublic(Authentication auth) {
        User user = getUserFromAuth(auth);
        return ResponseEntity.ok(anuntService.getAllAnunturiExceptCurrentUser(user.getId()));
    }

    @PostMapping("/rezervare")
    public ResponseEntity<String> rezervare(
            @RequestParam Long anuntId,
            @RequestParam String interval,
            Authentication auth) {
        try {
            // Verificare autentificare
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Trebuie să fii conectat pentru a face o rezervare");
            }

            User user = getUserFromAuth(auth);
            LocalTime ora = LocalTime.parse(interval);

            // Verificare existență anunț
            Anunt anunt = anuntRepository.findById(anuntId)
                    .orElseThrow(() -> new IllegalArgumentException("Anunț inexistent"));

            // Verificare dacă userul încearcă să-și rezerve propriul anunț
            if (anunt.getUser().getId().equals(user.getId())) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body("Nu puteți rezerva propriul anunț");
            }

            return anuntService.setProgramare(anuntId, user.getId(), ora);
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format oră invalid (HH:mm)");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{anuntId}/programariVIEW")
    public ResponseEntity<?> getProgramariForOwner(
            @PathVariable Long anuntId,
            Authentication auth) {
        try {
            User user = getUserFromAuth(auth);
            return ResponseEntity.ok(anuntService.getProgramariForOwner(anuntId, user.getId()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nu sunteți autentificat");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare internă la preluarea programărilor");
        }
    }

    @PostMapping("/anulare-rezervare")
    public ResponseEntity<String> anulareRezervare(
            @RequestParam Long anuntId,
            @RequestParam @Pattern(regexp = "^\\d{2}:\\d{2}$") String interval,
            Authentication auth) {

        try {
            User user = getUserFromAuth(auth);
            LocalTime ora = LocalTime.parse(interval);
            return anuntService.anulareRezervare(anuntId, ora, user.getId());
        } catch (DateTimeParseException e) {
            return ResponseEntity.badRequest().body("Format oră invalid");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{anuntId}/ore-disponibile")
    public ResponseEntity<?> getOreDisponibile(@PathVariable Long anuntId) {
        return anuntService.getOreDisponibile(anuntId);
    }

    @GetMapping("/profil")
    public ResponseEntity<?> getProfilUtilizator(Authentication auth) {
        try {
            User user = getUserFromAuth(auth);
            return anuntService.getProfilUtilizator(user.getId());
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nu sunteți autentificat");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Eroare la preluarea profilului");
        }
    }

    private User getUserFromAuth(Authentication auth) {
        return userRepository.findByUsername(auth.getName())
                .orElseThrow(() -> new UsernameNotFoundException("Utilizator negăsit"));
    }

    private ResponseEntity<String> unauthorizedResponse(UsernameNotFoundException e) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }

    private ResponseEntity<Map<String, String>> errorResponse(String error, Exception e) {
        return ResponseEntity.internalServerError()
                .body(Map.of("error", error, "details", e.getMessage()));
    }
}