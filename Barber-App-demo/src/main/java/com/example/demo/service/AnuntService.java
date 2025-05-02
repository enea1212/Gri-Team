package com.example.demo.service;

import com.example.demo.dto.*;
import com.example.demo.entity.*;
import com.example.demo.repository.*;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class AnuntService {
    private final AnuntRepository anuntRepository;
    private final FileStorageService fileStorageService;
    private final AnuntDisponibilitateRepository disponibilitateRepo;
    private final UserRepository userRepository;

    public AnuntService(AnuntRepository anuntRepository,
                        FileStorageService fileStorageService,
                        AnuntDisponibilitateRepository disponibilitateRepo,
                        UserRepository userRepository) {
        this.anuntRepository = anuntRepository;
        this.fileStorageService = fileStorageService;
        this.disponibilitateRepo = disponibilitateRepo;
        this.userRepository = userRepository;
    }

    @Transactional
    public AnuntDTO createAnunt(String titlu, String descriere, Double pret,
                                String locatie, MultipartFile poza, User user) {
        if (anuntRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("Limită 1 anunț/utilizator");
        }

        Anunt anunt = new Anunt();
        anunt.setTitlu(titlu.trim());
        anunt.setDescriere(descriere.trim());
        anunt.setPret(pret);
        anunt.setLocatie(locatie.trim());
        anunt.setUser(user);

        if (poza != null && !poza.isEmpty()) {
            anunt.setPoza(fileStorageService.storeFile(poza));
        }

        Anunt savedAnunt = anuntRepository.save(anunt);
        initializeazaDisponibilitati(savedAnunt);
        return convertToDTO(savedAnunt);
    }

    private void initializeazaDisponibilitati(Anunt anunt) {
        LocalTime start = LocalTime.of(10, 0);
        LocalTime end = LocalTime.of(18, 0);

        while (start.isBefore(end)) {
            AnuntDisponibilitate disp = new AnuntDisponibilitate();
            disp.setAnunt(anunt);
            disp.setIntervalOrar(start);
            disp.setDisponibil(true);
            disp.setOwner(anunt.getUser());
            disponibilitateRepo.save(disp);
            start = start.plusMinutes(30);
        }
    }

    @Transactional
    public ResponseEntity<String> deleteAnunt(Long id, User user) {
        Anunt anunt = anuntRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anunț inexistent"));

        if (!anunt.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Nu aveți permisiune");
        }

        // Șterge mai întâi disponibilitățile asociate
        disponibilitateRepo.deleteByAnuntId(id);

        // Apoi șterge anunțul
        anuntRepository.delete(anunt);

        return ResponseEntity.ok("Anunț șters cu succes");
    }

    public List<AnuntDTO> getAllAnunturiExceptCurrentUser(Long currentUserId) {
        return anuntRepository.findAllByUserIdNot(currentUserId).stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Transactional
    public ResponseEntity<String> setProgramare(Long anuntId, Long userId, LocalTime interval) {
        // Verifică dacă userul are deja o rezervare
        boolean hasExistingReservation = disponibilitateRepo.existsByReservedById(userId);
        if (hasExistingReservation) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Aveți deja o rezervare activă. Nu puteți face alte rezervări.");
        }

        Anunt anunt = anuntRepository.findById(anuntId)
                .orElseThrow(() -> new IllegalArgumentException("Anunț inexistent"));

        // Verifică dacă userul încearcă să-și rezerve propriul anunț
        if (anunt.getUser().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Nu puteți rezerva propriul anunț");
        }

        AnuntDisponibilitate disp = disponibilitateRepo
                .findByAnuntIdAndIntervalOrar(anuntId, interval)
                .orElseThrow(() -> new IllegalArgumentException("Interval invalid"));

        if (!disp.isDisponibil()) {
            return ResponseEntity.badRequest().body("Interval ocupat");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Utilizator invalid"));

        disp.setReservedBy(user);
        disp.setDisponibil(false);
        disponibilitateRepo.save(disp);

        return ResponseEntity.ok("Rezervat cu succes");
    }

    @Transactional
    public ResponseEntity<String> anulareRezervare(Long anuntId, LocalTime interval, Long userId) {
        AnuntDisponibilitate disp = disponibilitateRepo
                .findByAnuntIdAndIntervalOrar(anuntId, interval)
                .orElseThrow(() -> new IllegalArgumentException("Interval invalid"));



        if (disp.isDisponibil()) {
            return ResponseEntity.badRequest().body("Intervalul nu e rezervat");
        }

        if (!disp.getReservedBy().getId().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Nu puteți anula această rezervare");
        }
        

        disp.setReservedBy(null);
        disp.setDisponibil(true);
        disponibilitateRepo.save(disp);

        return ResponseEntity.ok("Rezervare anulată");
    }

    private AnuntDTO convertToDTO(Anunt anunt) {
        AnuntDTO dto = new AnuntDTO();
        dto.setId(anunt.getId());
        dto.setTitlu(anunt.getTitlu());
        dto.setDescriere(anunt.getDescriere());
        dto.setPret(anunt.getPret());
        dto.setLocatie(anunt.getLocatie());
        dto.setPoza(anunt.getPoza());

        UserDTO userDto = new UserDTO();
        userDto.setId(anunt.getUser().getId());
        userDto.setUsername(anunt.getUser().getUsername());
        dto.setUser(userDto);

        return dto;
    }

    private AnuntDisponibilitateDTO convertToDisponibilitateDTO(AnuntDisponibilitate disp) {
        AnuntDisponibilitateDTO dto = new AnuntDisponibilitateDTO();
        dto.setIntervalOrar(disp.getIntervalOrar());
        dto.setDisponibil(disp.isDisponibil());

        if (disp.getReservedBy() != null) {
            UserDTO userDTO = new UserDTO();
            userDTO.setId(disp.getReservedBy().getId());
            userDTO.setUsername(disp.getReservedBy().getUsername()); // Adaugă și username
            dto.setReservedBy(userDTO); // Nu uita să setezi userDTO în dto
        }
        return dto;
    }

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    public ResponseEntity<?> getOreDisponibile(Long anuntId) {
        // Verifică dacă anunțul există
        if (!anuntRepository.existsById(anuntId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Anunțul nu există");
        }

        List<String> oreDisponibile = disponibilitateRepo.findAllByAnuntId(anuntId).stream()
                .filter(AnuntDisponibilitate::isDisponibil)
                .map(disp -> disp.getIntervalOrar().toString())
                .collect(Collectors.toList());

        if (oreDisponibile.isEmpty()) {
            return ResponseEntity.ok("Nu există intervale disponibile pentru acest anunț");
        }

        return ResponseEntity.ok(oreDisponibile);
    }

    public List<AnuntDisponibilitateDTO> getProgramariForOwner(Long anuntId, Long ownerId) {
        log.info("Verificare programări pentru anunț {} de către owner {}", anuntId, ownerId);

        // Verifică existența anunțului
        Anunt anunt = anuntRepository.findById(anuntId)
                .orElseThrow(() -> {
                    log.error("Anunțul {} nu există", anuntId);
                    return new IllegalArgumentException("Anunțul nu există");
                });

        // Verifică dacă userul este proprietar
        if (!anunt.getUser().getId().equals(ownerId)) {
            log.warn("User {} a încercat să acceseze programări pentru anunț {} care îi aparține lui {}",
                    ownerId, anuntId, anunt.getUser().getId());
            throw new IllegalStateException("Nu sunteți proprietarul acestui anunț");
        }

        // Obține programările
        List<AnuntDisponibilitate> programari = disponibilitateRepo
                .findAllByAnuntIdAndDisponibilFalse(anuntId);

        if (programari.isEmpty()) {
            log.info("Nu există programări pentru anunțul {}", anuntId);
        }

        return programari.stream()
                .map(this::convertToDisponibilitateDTO)
                .collect(Collectors.toList());
    }
}