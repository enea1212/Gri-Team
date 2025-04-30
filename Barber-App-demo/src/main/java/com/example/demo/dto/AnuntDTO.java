package com.example.demo.dto;
import com.example.demo.dto.UserDTO;
import lombok.Data;

@Data
public class AnuntDTO {
    private Long id;
    private String titlu;
    private String descriere;
    private double pret;
    private String locatie;
    private String poza;
    private UserDTO user;

    // Add these setter methods
    public void setId(Long id) { this.id = id; }
    public void setTitlu(String titlu) { this.titlu = titlu; }
    public void setDescriere(String descriere) { this.descriere = descriere; }
    public void setPret(double pret) { this.pret = pret; }
    public void setLocatie(String locatie) { this.locatie = locatie; }
    public void setPoza(String poza) { this.poza = poza; }
    public void setUser(UserDTO user) { this.user = user; }


}