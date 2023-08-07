package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HospedagemDTO {

    private Long id;

    @NotBlank
    private String nomeHospede;

    @NotBlank
    private LocalDate dataInicio;

    @NotBlank
    private LocalDate dataFim;

    @NotBlank
    private Integer quantidadePessoas;

    @NotBlank
    private Status status;



}
