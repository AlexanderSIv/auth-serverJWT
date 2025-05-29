package com.epills.auth_server.service;


import com.epills.auth_server.dtos.EventDTO;
import com.epills.auth_server.entities.Event;
import com.epills.auth_server.repositories.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {
    private final EventRepository eventRepository;

    public void createEvent(String name, EventDTO eventDTO) {
        Event newEvent = new Event();
        newEvent.setName(name);
        newEvent.setDrug(eventDTO.getDrug());
        newEvent.setDate(eventDTO.getDate());
        newEvent.setGender(eventDTO.getGender());
        newEvent.setHeight(eventDTO.getHeight());
        newEvent.setWeight(eventDTO.getWeight());
        newEvent.setAge(eventDTO.getAge());
        newEvent.setSymptoms(eventDTO.getSymptoms());
        eventRepository.save(newEvent);
    }
}
