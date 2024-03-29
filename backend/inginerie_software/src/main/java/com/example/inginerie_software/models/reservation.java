package com.example.inginerie_software.models;



import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;


import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


@Entity
@Table(name = "reservations")
public class reservation implements Serializable {
    @Id
    @SequenceGenerator(name = "seq_reservations", sequenceName = "seq_reservation",
            allocationSize = 1)

    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_reservation")
    @Column(nullable = false, updatable = false)
    private Long id;

    private String userid;

    /*
    @ManyToOne
    @JoinColumn(name = "bike_id") // This is the foreign key column in the Reservation table
    private bikes bike;
*/
    @ManyToOne//(cascade = CascadeType.ALL)
    @JoinColumn(name="bike")
   // @JsonManagedReference
    private bikes bike;

    @Column(name = "stripeid", updatable = false)
    private String stripeid;

    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

    public reservation() {
    }

    public reservation(String userid, bikes bike, String stripeid) {
        this.userid = userid;
        this.bike = bike;
        this.stripeid =stripeid;
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public bikes getBike() {
        return bike;
    }

    public void setBike(bikes bike) {
        this.bike = bike;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getStripeid() {
        return stripeid;
    }
}

