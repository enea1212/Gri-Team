package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalTime;
import java.util.ArrayList;

@Entity
@Table(name = "anunt_disponibilitate")
@Data
public class AnuntDisponibilitate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "anunt_id", nullable = false)
    private Anunt anunt;

    @Column(name = "interval_orar", nullable = false)
    private LocalTime intervalOrar;

    @Column(name = "disponibil", nullable = false)
    private boolean disponibil;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;  // Always the ad owner

    @ManyToOne
    @JoinColumn(name = "reserved_by")
    private User reservedBy;  // Null when available




    // Getter și setter pentru disponibil
    public boolean isDisponibil() {
        return disponibil;
    }

    public void setDisponibil(boolean disponibil) {
        this.disponibil = disponibil;
    }

    // Getter și setter pentru intervalOrar
    public LocalTime getIntervalOrar() {
        return intervalOrar;
    }

    public void setIntervalOrar(LocalTime intervalOrar) {
        this.intervalOrar = intervalOrar;
    }
}
