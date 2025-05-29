package com.epills.auth_server.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DrugDTO {
    private Long id;
    private String mnn;
    private String pharmaGroup;
    private String name;
    private String symptoms;
}
