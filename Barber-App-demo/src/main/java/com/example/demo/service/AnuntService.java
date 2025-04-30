package com.example.demo.service;

import com.example.demo.entity.Anunt;
import com.example.demo.entity.User;
import com.example.demo.repository.AnuntRepository;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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
    public Anunt createAnunt(Anunt anunt, MultipartFile poza) {
        
        if (anuntRepository.existsByUserId(anunt.getUser().getId())) {
            throw new IllegalStateException("Ați depășit limita de 1 anunț per utilizator");
        }
        if (poza != null && !poza.isEmpty()) {
            String fileName = fileStorageService.storeFile(poza);
            anunt.setPoza(fileName);
        }
        return anuntRepository.save(anunt);
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
}