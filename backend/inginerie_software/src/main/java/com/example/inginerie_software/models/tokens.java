package com.example.inginerie_software.models;


import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "tokens")
public class tokens {
    @Id
    @SequenceGenerator(name = "seq_tokens", sequenceName = "seq_tokens",
            allocationSize = 1)


    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seq_tokens")
    @Column(nullable = false, updatable = false)
    private Long id;

    @Column(nullable = false)
    private String value;

    @Column(nullable = false)
    private String current_email;

    @Column(nullable = false)
    private String new_email;

    @Column(name="expiry_date")
    private LocalDateTime expiryDate;

    public Long getId() {
        return id;
    }

    public tokens() {
        this.value = UUID.randomUUID().toString();
        this.expiryDate = LocalDateTime.now().plusHours(1);
    }

    public tokens(String current_email, String new_email) {
        this.value = UUID.randomUUID().toString();
        this.current_email = current_email;
        this.new_email = new_email;
        this.expiryDate = LocalDateTime.now().plusHours(1);

    }

    public String getValue() {
        return value;
    }


    public String getCurrent_email() {
        return current_email;
    }

    public void setCurrent_email(String current_email) {
        this.current_email = current_email;
    }

    public String getNew_email() {
        return new_email;
    }

    public void setNew_email(String new_email) {
        this.new_email = new_email;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }
}
