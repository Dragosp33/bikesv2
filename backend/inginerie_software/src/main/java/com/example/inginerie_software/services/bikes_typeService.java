package com.example.inginerie_software.services;

import com.example.inginerie_software.exceptions.bikes_typeException;
import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.repositories.bikes_typeRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class bikes_typeService {

    private final com.example.inginerie_software.repositories.bikes_typeRepository bikes_typeRepository;

    @Autowired
    public bikes_typeService(bikes_typeRepository b) { this.bikes_typeRepository = b;}

    public List<bikes_type> getBikes_type() { return bikes_typeRepository.findAll();}

    public bikes_type getBike_typeById(Long id) { return bikes_typeRepository.findBikesTypeById(id).
            orElseThrow(() -> new bikes_typeException("nu a fost gasita"));}

    public bikes_type addBike_type(bikes_type b) {
        return bikes_typeRepository.save(b);
    }

}
