package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.Reserva;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record UsuarioDTO(
        @NotBlank Long id,
        @NotBlank String usuario,
        @NotBlank String senha,
        List<Reserva> reservas
) {
}
