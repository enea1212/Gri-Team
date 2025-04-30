package com.example.demo.service;

import com.example.demo.dto.AnuntDTO;
import com.example.demo.dto.UserDTO;
import com.example.demo.entity.Anunt;
import com.example.demo.entity.User;
import com.example.demo.repository.AnuntRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


import java.util.List;
import java.util.stream.Collectors;

@Service
public class AnuntService {
    private final AnuntRepository anuntRepository;
    private final FileStorageService fileStorageService;

    public AnuntService(AnuntRepository anuntRepository,
                        FileStorageService fileStorageService) {
        this.anuntRepository = anuntRepository;
        this.fileStorageService = fileStorageService;
    }



    @Transactional
    public AnuntDTO createAnunt(String titlu, String descriere, Double pret,
                                String locatie, MultipartFile poza, User user) {
        if (anuntRepository.existsByUserId(user.getId())) {
            throw new IllegalStateException("Ați depășit limita de 1 anunț per utilizator");
        }

        Anunt anunt = new Anunt();
        anunt.setTitlu(titlu.trim());
        anunt.setDescriere(descriere.trim());
        anunt.setPret(pret);
        anunt.setLocatie(locatie.trim());
        anunt.setUser(user);

        if (poza != null && !poza.isEmpty()) {
            String fileName = fileStorageService.storeFile(poza);
            anunt.setPoza(fileName);
        }

        Anunt savedAnunt = anuntRepository.save(anunt);
        return convertToDTO(savedAnunt);
    }

    @Transactional
    public ResponseEntity<String> deleteAnunt(Long id, User user) {
        Anunt anunt = anuntRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Anunțul nu există"));

        if (!anunt.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Nu aveți permisiunea să ștergeți acest anunț");
        }

        anuntRepository.deleteById(id);
        return ResponseEntity.ok("Anunțul a fost șters cu succes");
    }

    public List<AnuntDTO> getAllAnunturiExceptCurrentUser(Long currentUserId) {
        List<Anunt> anunturi = anuntRepository.findAllByUserIdNot(currentUserId);
        return anunturi.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    private AnuntDTO convertToDTO(Anunt anunt) {
        AnuntDTO dto = new AnuntDTO();
        dto.setId(anunt.getId());
        dto.setTitlu(anunt.getTitlu());
        dto.setDescriere(anunt.getDescriere());
        dto.setPret(anunt.getPret());
        dto.setLocatie(anunt.getLocatie());
        dto.setPoza(anunt.getPoza());

        if (anunt.getUser() != null) {
            UserDTO userDto = new UserDTO();  // Use the proper UserDTO class
            userDto.setId(anunt.getUser().getId());
            userDto.setUsername(anunt.getUser().getUsername());
            dto.setUser(userDto);
        }

        return dto;
    }
}