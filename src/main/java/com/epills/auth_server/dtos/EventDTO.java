package com.epills.auth_server.dtos;


import lombok.Data;

@Data
public class EventDTO {
    private Long id;
    private String drug;
    private String date;
    private Integer gender;
    private Integer height;
    private Integer weight;
    private Integer age;
    private String symptoms;
}
