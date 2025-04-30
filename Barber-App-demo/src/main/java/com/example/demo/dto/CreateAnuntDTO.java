package com.example.demo.dto;

import lombok.Data;

@Data
public class CreateAnuntDTO {
    private String titlu;
    private String descriere;
    private Double pret;
    private String locatie;
}