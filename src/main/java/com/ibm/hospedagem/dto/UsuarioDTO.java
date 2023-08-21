package com.ibm.hospedagem.dto;

import com.ibm.hospedagem.model.Hospedagem;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class UsuarioDTO {

    private Long id;

    @NotBlank
    private String username;

    @NotBlank
    @Size(min = 6, max = 15)
    private String password;

    private List<Hospedagem> hospedagens;

}
