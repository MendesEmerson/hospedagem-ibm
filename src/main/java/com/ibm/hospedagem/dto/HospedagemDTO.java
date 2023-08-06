package com.ibm.hospedagem.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class HospedagemDTO {

    private Long id;

    @NotBlank
    private String nomeHospede;

    @NotBlank
    private Date dataInicio;

    @NotBlank
    private Date dataFinal;

    @NotBlank
    private Integer quantidadePessoas;

    @NotBlank
    private String status;

}
