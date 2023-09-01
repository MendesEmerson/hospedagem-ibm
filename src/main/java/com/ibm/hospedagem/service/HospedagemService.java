package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.HospedagemDTO;

public interface HospedagemService {
    HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO);
    HospedagemDTO findHospedagemById(Long id);
    HospedagemDTO updateHospedagemById(Long id, HospedagemDTO hospedagemDTO);
    void deleteById(Long id);
}
