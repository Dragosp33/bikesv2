package com.example.inginerie_software.services;

import com.example.inginerie_software.models.reservation;
import com.example.inginerie_software.repositories.reservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class reservationService {
    private final reservationRepository reservationRepository;

    @Autowired
    public reservationService(reservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public List<reservation> getReservationsByUserid(String userid) {
        return reservationRepository.findByUserid(userid);
    }
}