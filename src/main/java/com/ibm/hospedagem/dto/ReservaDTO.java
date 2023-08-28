package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.model.Usuario;
import com.ibm.hospedagem.model.enums.Status;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;

import java.time.LocalDate;


public record ReservaDTO(
        Long id,
        @NotBlank String nomeHospede,
        @NotBlank String dataInicio,
        @NotBlank String dataFim,
        Integer quantidadePessoas,
        Double valorTotalReserva,
        Status status,
        Hospedagem hospedagem,
        Usuario usuario
) {
}
