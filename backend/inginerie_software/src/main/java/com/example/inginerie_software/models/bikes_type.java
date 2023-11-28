package com.example.inginerie_software.models;



import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "bikes_type")
public class bikes_type implements Serializable {
    @Id
    @SequenceGenerator(name = "seq_bikes_type", sequenceName = "seq_bikes_type",
            allocationSize = 1)


    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_bikes_type")
    @Column(nullable = false, updatable = false)
    private Long id;

    @NotNull
    @Size(min = 3, max = 40)
    private String nume;

    @NotNull
    private long price;

    private String image_url;

    @OneToMany(
            cascade = CascadeType.ALL
    )
    @JoinColumn(name = "bikes_id")
    private List<bikes> bikes_id = new ArrayList<>();


    public bikes_type() {
    }

    public bikes_type(Long id, String nume, long price, String image_url) {
        this.id = id;
        this.nume = nume;
        this.price = price;
        this.image_url = image_url;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNume() {
        return nume;
    }

    public void setNume(String nume) {
        this.nume = nume;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
