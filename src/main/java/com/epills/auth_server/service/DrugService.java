package com.epills.auth_server.service;


import com.epills.auth_server.dtos.DrugDTO;
import com.epills.auth_server.entities.Drug;
import com.epills.auth_server.repositories.DrugRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DrugService {
    private final DrugRepository drugRepository;

    public Optional<Drug> findByName(String name) {
        return drugRepository.findByName(name);
    }

    public Drug addDrug(DrugDTO drugDTO){
        Drug newDrug = new Drug();
        newDrug.setName(drugDTO.getName());
        newDrug.setMnn(drugDTO.getMnn());
        newDrug.setSymptoms(drugDTO.getSymptoms());
        newDrug.setPharmaGroup(drugDTO.getPharmaGroup());
        return drugRepository.save(newDrug);
    }
}
