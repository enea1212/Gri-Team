package com.example.demo.dto;

public class AnuntRequest {
    private String titlu;
    private String descriere;
    private Double pret;
    private String pozaBase64;
    private String pozaFileName;

    // Getters and setters
    public String getTitlu() { return titlu; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public String getDescriere() { return descriere; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public Double getPret() { return pret; }
    public void setPret(Double pret) { this.pret = pret; }
    public String getPozaBase64() { return pozaBase64; }
    public void setPozaBase64(String pozaBase64) { this.pozaBase64 = pozaBase64; }
    public String getPozaFileName() { return pozaFileName; }
    public void setPozaFileName(String pozaFileName) { this.pozaFileName = pozaFileName; }
}