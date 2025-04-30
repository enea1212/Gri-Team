package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Entity
@Table(name = "anunturi")
@Data
public class Anunt {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Titlul este obligatoriu")
    @Size(max = 100, message = "Titlul nu poate depăși 100 de caractere")
    @Column(nullable = false, length = 100)
    private String titlu;

    @NotBlank(message = "Descrierea este obligatorie")
    @Size(max = 1000, message = "Descrierea nu poate depăși 1000 de caractere")
    @Column(nullable = false, length = 1000)
    private String descriere;

    @NotNull(message = "Prețul este obligatoriu")
    @Positive(message = "Prețul trebuie să fie pozitiv")
    @Column(nullable = false)
    private Double pret;

    @NotBlank(message = "Locația este obligatorie")
    @Size(max = 100, message = "Locația nu poate depăși 100 de caractere")
    @Column(name = "locatie", nullable = false, length = 100)
    private String locatie;

    @Column(length = 255)
    private String poza;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
}