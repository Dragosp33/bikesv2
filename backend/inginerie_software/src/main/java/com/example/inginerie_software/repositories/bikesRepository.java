package com.example.inginerie_software.repositories;

import com.example.inginerie_software.models.bikes;
import com.example.inginerie_software.models.bikes_type;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface bikesRepository extends JpaRepository<bikes, Long> {
    Optional<bikes> findBikesById(Long id);

    List<bikes> findByAvailableFalse();

    List<bikes> findByAvailableTrue();

}
