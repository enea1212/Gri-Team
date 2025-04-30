package com.example.demo.dto;

import java.time.LocalDateTime;

public class AnuntResponse {
    private Long id;
    private String titlu;
    private String descriere;
    private Double pret;
    private String pozaBase64;
    private String pozaFileName;
    private String username;
    private LocalDateTime createdAt;

    public AnuntResponse(Long id, String titlu, String descriere, Double pret,
                         String pozaBase64, String pozaFileName,
                         String username, LocalDateTime createdAt) {
        this.id = id;
        this.titlu = titlu;
        this.descriere = descriere;
        this.pret = pret;
        this.pozaBase64 = pozaBase64;
        this.pozaFileName = pozaFileName;
        this.username = username;
        this.createdAt = createdAt;
    }

    // Getters
    public Long getId() { return id; }
    public String getTitlu() { return titlu; }
    public String getDescriere() { return descriere; }
    public Double getPret() { return pret; }
    public String getPozaBase64() { return pozaBase64; }
    public String getPozaFileName() { return pozaFileName; }
    public String getUsername() { return username; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}