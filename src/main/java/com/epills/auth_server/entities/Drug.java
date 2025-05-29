package com.epills.auth_server.entities;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "drugs")
public class Drug {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "mnn")
    private String mnn;

    @Column(name = "pharma_group")
    private String pharmaGroup;

    @Column(name = "trade_name")
    private String name;

    @Column(name = "symptoms")
    private String symptoms;
}
