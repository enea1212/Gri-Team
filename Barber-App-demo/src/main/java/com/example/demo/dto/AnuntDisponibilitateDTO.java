package com.example.demo.dto;

import java.time.LocalTime;

public class AnuntDisponibilitateDTO {

    private LocalTime intervalOrar;
    private boolean disponibil;
    private Long ownerId;
    private Long reservedByUserId; // ID-ul utilizatorului pentru a-l trimite la front-end
    private String reservedByUserName;
    private UserDTO reservedBy;

    // Getter și setter pentru intervalOrar
    public LocalTime getIntervalOrar() {
        return intervalOrar;
    }

    public void setIntervalOrar(LocalTime intervalOrar) {
        this.intervalOrar = intervalOrar;
    }

    // Getter și setter pentru disponibil
    public boolean isDisponibil() {
        return disponibil;
    }

    public void setDisponibil(boolean disponibil) {
        this.disponibil = disponibil;
    }

    public void setOwnerId(Long userId) {
        this.ownerId = ownerId;
    }

    public void setReservedByUserId(Long userId) {
        this.reservedByUserId = reservedByUserId;
    }

    public void setReservedByUsername(String reservedByUserName)
    {
        this.reservedByUserName = reservedByUserName;
    }

    public void setReservedBy(UserDTO reservedBy) {
        this.reservedBy = reservedBy;
    }
}
