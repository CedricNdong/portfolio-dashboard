package com.portfolio.backend.application.data;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class SampleResume extends AbstractEntity {

    private String occupation;
    private LocalDate occupationStart;
    private LocalDate occupationEnd;
    private String occupationPlace;
    private String city;
    private String country;
    private String description;
    private String specialisation;

    public String getOccupation() {
        return occupation;
    }
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    public LocalDate getOccupationStart() {
        return occupationStart;
    }
    public void setOccupationStart(LocalDate occupationStart) {
        this.occupationStart = occupationStart;
    }
    public LocalDate getOccupationEnd() {
        return occupationEnd;
    }
    public void setOccupationEnd(LocalDate occupationEnd) {
        this.occupationEnd = occupationEnd;
    }
    public String getOccupationPlace() {
        return occupationPlace;
    }
    public void setOccupationPlace(String occupationPlace) {
        this.occupationPlace = occupationPlace;
    }
    public String getCity() {
        return city;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getSpecialisation() {
        return specialisation;
    }
    public void setSpecialisation(String specialisation) {
        this.specialisation = specialisation;
    }

}
