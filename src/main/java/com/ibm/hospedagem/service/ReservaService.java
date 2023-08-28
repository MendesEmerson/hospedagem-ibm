package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.model.enums.Status;

import java.time.LocalDate;
import java.util.List;

public interface ReservaService {

    List<ReservaDTO> findAll();
    List<ReservaDTO> findByStatus(Status status);
    List<LocalDate> findDiasIndisponiveis();
    ReservaDTO findById(Long id);
    ReservaDTO createReserva(Long hospedagemId, Long usuarioId, ReservaDTO reservaDTO);
    ReservaDTO updateById(Long id, ReservaDTO reservaDTO);
    ReservaDTO deleteById(Long id);

}
