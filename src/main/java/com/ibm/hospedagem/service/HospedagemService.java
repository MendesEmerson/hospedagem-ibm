package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.HospedagemDTO;

import java.util.List;

public interface HospedagemService {

    List<HospedagemDTO> findAll();
    HospedagemDTO findById(Long id);
    HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO);
    HospedagemDTO updateById(Long id, HospedagemDTO hospedagemDTO);
    void deleteById(Long id);

}
