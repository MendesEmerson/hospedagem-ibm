package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.enums.Status;

import java.time.LocalDate;
import java.util.List;

public interface HospedagemService {

    List<HospedagemDTO> findAll();
    List<HospedagemDTO> findByStatus(Status status);
    List<LocalDate> findDiasIndisponiveis();
    HospedagemDTO findById(Long id);
    HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO);
    HospedagemDTO updateById(Long id, HospedagemDTO hospedagemDTO);
    HospedagemDTO deleteById(Long id);

}
