package com.example.demo.dto;

public class AnuntRezervatDTO {
    private AnuntDTO anunt;
    private String intervalRezervat;

    // Getters and setters
    public AnuntDTO getAnunt() {
        return anunt;
    }

    public void setAnunt(AnuntDTO anunt) {
        this.anunt = anunt;
    }

    public String getIntervalRezervat() {
        return intervalRezervat;
    }

    public void setIntervalRezervat(String intervalRezervat) {
        this.intervalRezervat = intervalRezervat;
    }
}