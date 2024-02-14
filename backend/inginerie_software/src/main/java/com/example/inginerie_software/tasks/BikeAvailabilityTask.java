package com.example.inginerie_software.tasks;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import com.example.inginerie_software.models.bikes;
import com.example.inginerie_software.models.reservation;
import com.example.inginerie_software.services.bikesService;
import com.example.inginerie_software.services.reservationService;

@Component
public class BikeAvailabilityTask {

    private final bikesService bikesService;
    private final reservationService reservationService;

    public BikeAvailabilityTask(bikesService bikesService, reservationService reservationService) {
        this.bikesService = bikesService;
        this.reservationService = reservationService;
    }

    @Scheduled(fixedRate = 60000) // Runs every minute
    public void checkBikeAvailability() {
        System.out.println();
        System.out.println("---------------------------------------------");
        System.out.println("SCHEDULED TASK RUNNING...CHECKING FOR BIKES...");
        List<bikes> unavailableBikes = bikesService.getUnavailableBikes();
        if(!unavailableBikes.isEmpty()){
            System.out.println("UNAVAILABLE BIKES FOUND...CHECKING FOR EXPIRY TIME...");
        }

        for (bikes bike : unavailableBikes) {
            try {
            Optional<reservation> latestReservation = reservationService.getLatestReservationByBikeId(bike.getId());

                if(latestReservation.isPresent() && latestReservation.get().getExpiryDate().isBefore(LocalDateTime.now())) {
                // Reservation expired, make the bike available again
                    System.out.println("FOUND EXPIRED RESERVATION...MAKING BIKE AVAILABLE AGAIN... " + latestReservation.get().getExpiryDate() + latestReservation.get().getBike().getId());
                bike.setAvailable(true);
                bikesService.saveBike(bike);

                // setting the bike's reservations to an empty list
                // bike.setReservations(new ArrayList<>());
            }
                else if (latestReservation.isEmpty()){
                    System.out.println("FOUND UNAVAILABLE BIKE WITH NO RESERVATION...MAKING BIKE AVAILABLE AGAIN... ");
                    bike.setAvailable(true);
                    bikesService.saveBike(bike);
                }

                else {
                    System.out.println("NO EXPIRED RESERVATION FOUND...CHECKING AGAIN IN 60 SECONDS...");
                }
        }
            catch(Exception e) {
                e.printStackTrace();
                System.out.println("BACKGROUND TASK ENDED ABRUPTLY. ERROR MESSAGE: " + e.getMessage());
            }
        }
    }
}