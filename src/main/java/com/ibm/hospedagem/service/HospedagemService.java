package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.enums.Status;

import java.util.List;

public interface HospedagemService {

    List<HospedagemDTO> findAll();
    List<HospedagemDTO> findByStatus(Status status);
    HospedagemDTO findById(Long id);
    HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO);
    HospedagemDTO updateById(Long id, HospedagemDTO hospedagemDTO);
    void deleteById(Long id);

}
