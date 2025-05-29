package com.epills.auth_server.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "drug")
    private String drug;

    @Column(name = "date")
    private String date;

    @Column(name = "gender")
    private Integer gender;

    @Column(name = "height")
    private Integer height;

    @Column(name = "weight")
    private Integer weight;

    @Column(name = "age")
    private Integer age;

    @Column(name = "symptoms")
    private String symptoms;
}
