package com.example.inginerie_software.services;

import com.example.inginerie_software.exceptions.bikes_typeException;
import com.example.inginerie_software.models.bikes;
import com.example.inginerie_software.models.bikes_type;


import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import com.example.inginerie_software.repositories.bikesRepository;


import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class bikesService {

    private final bikesRepository bikesRepository;
    private final bikes_typeService bikes_typeService;

    @Autowired
    public bikesService(bikes_typeService bts, bikesRepository bR) {
        this.bikesRepository = bR;
        this.bikes_typeService = bts;
    }

    public bikes add_bike(bikes b) {
        bikes_type bt = this.bikes_typeService.getBike_typeById(b.getTip().getId());
        b.setTip(bt);
        return bikesRepository.save(b);

    }
    public bikes getBikeById(Long id) { return bikesRepository.findBikesById(id).
            orElseThrow(() -> new bikes_typeException("nu a fost gasita"));}




    public List<bikes> getBikes() {
        return bikesRepository.findAll();
    }
}
