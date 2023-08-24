package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.Usuario;
import com.ibm.hospedagem.model.enums.Status;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public record ReservaDTO(
        Long id,
        @NotBlank String nomeHospede,
        @NotBlank LocalDate dataInicio,
        @NotBlank LocalDate dataFim,
        @NotBlank Integer quantidadePessoas,
        @NotBlank Usuario usuario,
        @NotBlank Status status
) {
}
