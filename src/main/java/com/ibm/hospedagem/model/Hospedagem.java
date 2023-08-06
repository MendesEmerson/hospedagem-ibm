package com.ibm.hospedagem.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
public class Hospedagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomeHospede;

    @Column(nullable = false)
    private Date dataInicio;

    @Column(nullable = false)
    private Date dataFinal;

    @Column(nullable = false)
    private Integer quantidadePessoas;

    @Column(nullable = false)
    private String status;
}
