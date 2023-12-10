package com.example.inginerie_software.services;

import com.example.inginerie_software.models.bikes;
import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.models.reservation;
import com.example.inginerie_software.repositories.reservationRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
public class reservationService {
    private final reservationRepository reservationRepository;
    private final bikesService bikesService;

    @Autowired
    public reservationService(reservationRepository reservationRepository, bikesService bS) {
        this.reservationRepository = reservationRepository;
        this.bikesService = bS;
    }

    public List<reservation> getReservationsByUserid(String userid) {
        return reservationRepository.findByUserid(userid);
    }

   /* public bikes add_reservation(reservation res) {
        bikes_type bt = this.bikesService.getBikes(res.getTip().getId());
        res.setBike(bt);
        return bikesRepository.save(b);

    }*/

   public reservation createReservation(String userId, bikes bike, String stripeid) {
       //LocalDateTime expiryDate = LocalDateTime.now().plusHours(1);
       reservation reservation = new reservation(userId, bike, stripeid);
       return reservationRepository.save(reservation);
   }

    public reservation getReservationByStripeid(String stripeid) throws Exception {
        reservation res = reservationRepository.findByStripeid(stripeid);
        if (res == null) {
            throw new NoSuchElementException("Reservation not found ");
        }
        return res;

    }

    public List<reservation> getReservations() {
        List<reservation> k = reservationRepository.findAll();
        System.out.println("RESERVATIONS::::::::::::::" + k);
        return k;
    }

    public Optional<reservation> getHighestExpiryReservationForUser(String userid) {
        LocalDateTime now = LocalDateTime.now();
        return reservationRepository.findHighestExpiryAfterNowForUser(userid, now);
    }

    public Optional<reservation> getLatestReservationByBikeId(Long bikeId) {
        return reservationRepository.findTopByBikeIdOrderByExpiryDateDesc(bikeId);
    }

    public void saveReservation(reservation reservation) {
        reservationRepository.save(reservation);
    }
}