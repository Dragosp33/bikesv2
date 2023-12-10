package com.example.inginerie_software.repositories;

import com.example.inginerie_software.models.reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface reservationRepository extends JpaRepository<reservation, Long> {
    List<reservation> findByUserid(String userid);

    reservation findByStripeid(String stripeid);

    List<reservation> findByExpiryDateGreaterThan(LocalDateTime currentDateTime);

    @Query("SELECT r FROM reservation r " +
            "WHERE r.userid = :userid AND r.expiryDate > :now " +
            "ORDER BY r.expiryDate DESC")
    Optional<reservation> findHighestExpiryAfterNowForUser(
            @Param("userid") String userid,
            @Param("now") LocalDateTime now
    );

    Optional<reservation> findTopByBikeIdOrderByExpiryDateDesc(Long bikeId);
}