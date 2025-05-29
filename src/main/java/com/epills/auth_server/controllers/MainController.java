package com.epills.auth_server.controllers;


import com.epills.auth_server.dtos.DrugDTO;
import com.epills.auth_server.entities.Drug;
import com.epills.auth_server.exceptions.ApplicationError;
import com.epills.auth_server.service.DrugService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
public class MainController {
    private final DrugService drugService;

    @GetMapping("/unsecured")
    public String unsecuredData() {
        return "unsecured";
    }

    @GetMapping("/secured")
    public String securedData() {
        return "secured";
    }

    @GetMapping("/admin")
    public String adminData() {
        return "for administrator only";
    }

    @GetMapping("/info")
    public String infoData(Principal principal) {
        return principal.getName();
    }

    @GetMapping("/drugs/{drugName}")
    public ResponseEntity<?> getDrugInfo(
            @PathVariable("drugName") String drugName
    ){
        if(drugService.findByName(drugName).isEmpty()){
            return new ResponseEntity<>(new ApplicationError(HttpStatus.BAD_REQUEST.value(), "Лекаство с таким названием не найдено"), HttpStatus.BAD_REQUEST);
        }
        Optional<Drug> drug = drugService.findByName(drugName);
        return ResponseEntity.ok(drug);
    }

    @PostMapping("/add-drug")
    public ResponseEntity<?> addNewDrug(@RequestBody DrugDTO drugDTO){
        if(drugService.findByName(drugDTO.getName()).isPresent()){
            return new ResponseEntity<>(new ApplicationError(HttpStatus.BAD_REQUEST.value(), "Лекаство с таким названием уже есть"), HttpStatus.BAD_REQUEST);
        }
        Drug newDrug = drugService.addDrug(drugDTO);
        return ResponseEntity.ok("Запись о лекарстве успешно создана");
    }

}
