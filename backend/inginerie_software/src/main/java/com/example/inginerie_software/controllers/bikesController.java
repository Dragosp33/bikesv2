package com.example.inginerie_software.controllers;

import com.example.inginerie_software.models.bikes;
import com.example.inginerie_software.models.bikes_type;
import com.example.inginerie_software.services.bikesService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/bikes")
public class bikesController {

    private final bikesService bikesService;

    public bikesController(bikesService bs) { this.bikesService = bs;}

    @GetMapping
    public ResponseEntity<List<bikes>> getallbikes(){
        List<bikes> bikes = bikesService.getBikes();
        return new ResponseEntity<>(bikes, HttpStatus.OK);
    }

    @PostMapping(path = "/add")
    public @ResponseBody
    String addBike(@Valid @RequestBody bikes b1, BindingResult bindingResult){
        if (bindingResult.hasErrors()) { return "error"; }
        bikesService.add_bike(b1);
        return "saved";
    }
}
