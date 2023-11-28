package com.example.inginerie_software.repositories;

import com.example.inginerie_software.models.bikes_type;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface bikes_typeRepository extends JpaRepository<bikes_type, Long> {
    Optional<bikes_type> findBikesTypeById(Long id);
}
