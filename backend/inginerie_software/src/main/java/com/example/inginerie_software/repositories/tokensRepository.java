package com.example.inginerie_software.repositories;


import com.example.inginerie_software.models.tokens;
import org.springframework.data.jpa.repository.JpaRepository;

public interface tokensRepository extends JpaRepository<tokens, Long> {
    tokens findByValue(String value);
}
