package com.example.inginerie_software.repositories;

import com.example.inginerie_software.models.reservation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface reservationRepository extends JpaRepository<reservation, Long> {
    List<reservation> findByUserid(String userid);
}