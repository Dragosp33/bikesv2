package com.example.inginerie_software.controllers;


import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.services.bikes_typeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bikes_type")
public class bikes_typeController {

    private final com.example.inginerie_software.services.bikes_typeService bikes_typeService;

    public bikes_typeController(bikes_typeService bS) { this.bikes_typeService = bS;}

    @GetMapping
    public ResponseEntity<List<bikes_type>> getallbikes_type(){
        List<bikes_type> bikes_type = bikes_typeService.getBikes_type();
        return new ResponseEntity<>(bikes_type, HttpStatus.OK);
    }

    @PostMapping(path="/add") // Map ONLY POST Requests
    public @ResponseBody
    String addNewCursa (@Valid @RequestBody bikes_type B2,
                        BindingResult bindingResult)  {

        if(bindingResult.hasErrors()) {
            //com.example.inginerie_software.services.bikes_typeService.validateCursa(B2);
            return "error";
        }
        /* TODO:
            1) DTO - DATA TRANSFER OBJECT - CURSEDTO (CARE SA CONTINA DOAR DATELE DE AFISAJ) (OPTIONAL)
            2) IMPORTANT!!!!!!!!
                CREATE CLASS - CURSEVALIDATOR ;
                VALIDARE - DESTINATIE, PLECARE, ETC.
                VALIDAREA CURSELOR VA FI FOLOSITA IN CURSESERVICE SAU
            \   AICI INAINTEA TESTARII DIN CURSESERVICE.ADDCURSA
                DACA NU SE GASESTE NICIO EROARE IN METODA VALIDARE(CURSA)
                VA FI TESTAT BINDING.HASERRORS CARE VA RETURNA O EROARE SIMPLA
                DACA TRECE SI DE BINDING.HASERRORS, O A 3-A TESTARE VA AVEA LOC
                IN CURSESERVICE.ADDCURSA(CURSA);


         */



        bikes_typeService.addBike_type(B2);
        return "Saved";
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<bikes_type> getCursaById (@PathVariable("id") Long id) {

        bikes_type c = bikes_typeService.getBike_typeById(id);
        return new ResponseEntity<>(c, HttpStatus.OK);

    }
}
