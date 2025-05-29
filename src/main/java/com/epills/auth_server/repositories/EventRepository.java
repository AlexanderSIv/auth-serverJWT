package com.epills.auth_server.repositories;

import com.epills.auth_server.entities.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {
}
