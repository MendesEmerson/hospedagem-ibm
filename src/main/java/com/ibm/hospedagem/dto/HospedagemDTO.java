package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.Reserva;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.ArrayList;
import java.util.List;

public record HospedagemDTO(
        Long id,
        @NotBlank String titulo,
        @NotBlank String descricao,
        @NotNull Double valorDiaria,
        @NotBlank List<String> comodidades,
        @NotBlank List<Reserva> reservas
) {
}
