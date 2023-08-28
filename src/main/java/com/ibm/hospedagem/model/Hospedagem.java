package com.ibm.hospedagem.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@JsonIgnoreProperties({"reservas"})
public class Hospedagem implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private Double valorDiaria;

    @Column(nullable = false)
    private List<String> comodidades;

    @OneToMany(mappedBy = "hospedagem", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;

}
