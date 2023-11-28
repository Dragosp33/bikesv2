package com.example.inginerie_software.models;

import jakarta.persistence.*;

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

    public bikes() {
    }

    public bikes(Long id, String longitudine, String latitudine, bikes_type tip) {
        this.id = id;
        this.longitudine = longitudine;
        this.latitudine = latitudine;
        this.tip = tip;
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
}
