package com.example.inginerie_software.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bikes")
public class bikes {
    @Id
    @SequenceGenerator(name = "seq_biciclete", sequenceName = "seq_biciclete",
            allocationSize = 1)


    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_biciclete")
    @Column(nullable = false, updatable = false)
    private Long id;
    private String longitudine;
    private String latitudine;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name="tip")
    private bikes_type tip;


    /*
    @OneToMany (mappedBy = "bike")
    @JsonBackReference
    private List<reservation> reservations = new ArrayList<>();*/
    @OneToMany(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "res")
    private List<reservation> reservations = new ArrayList<>();

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean available;

    public bikes() {
        this.available = true;
    }

    public bikes(Long id, String longitudine, String latitudine, bikes_type tip) {
        this.id = id;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.tip = tip;
        this.available = true;

    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLongitudine() {
        return longitudine;
    }

    public void setLongitudine(String longitudine) {
        this.longitudine = longitudine;
    }

    public String getLatitudine() {
        return latitudine;
    }

    public void setLatitudine(String latitudine) {
        this.latitudine = latitudine;
    }

    public bikes_type getTip() {
        return tip;
    }

    public void setTip(bikes_type tip) {
        this.tip = tip;
    }

    public List<reservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<reservation> reservations) {
        this.reservations = reservations;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
