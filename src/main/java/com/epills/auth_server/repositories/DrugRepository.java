package com.epills.auth_server.repositories;

import com.epills.auth_server.entities.Drug;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DrugRepository extends CrudRepository<Drug, Long>  {
    Optional<Drug> findByName(String name);
}
