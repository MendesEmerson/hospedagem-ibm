package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.repository.HospedagemRepository;
import com.ibm.hospedagem.service.HospedagemService;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class HospedagemServiceImpl implements HospedagemService {

    @Autowired
    private HospedagemRepository hospedagemRepository;

    @Override
    public HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO) {

        verificarCampos(hospedagemDTO);

        Hospedagem hospedagem = toEntity(hospedagemDTO);
        hospedagem.setReservas(new ArrayList<>());
        hospedagemRepository.save(hospedagem);

        return toDTO(hospedagem);
    }

    @Override
        public HospedagemDTO findHospedagemById(Long id) {
            Hospedagem hospedagem = hospedagemRepository.findById(id)
                    .orElseThrow(() -> new HospedagemNotFoundException("Hospedagem não encontrada"));
            return toDTO(hospedagem);
        }

    private HospedagemDTO toDTO(Hospedagem hospedagem) {
        return new HospedagemDTO(
                hospedagem.getId(),
                hospedagem.getTitulo(),
                hospedagem.getDescricao(),
                hospedagem.getValorDiaria(),
                hospedagem.getComodidades(),
                hospedagem.getReservas()
        );
    }

    private Hospedagem toEntity(HospedagemDTO hospedagemDTO) {
        return new Hospedagem(
                hospedagemDTO.id(),
                hospedagemDTO.titulo(),
                hospedagemDTO.descricao(),
                hospedagemDTO.valorDiaria(),
                hospedagemDTO.comodidades(),
                hospedagemDTO.reservas()
        );
    }

    private void verificarCampos(HospedagemDTO hospedagemDTO) {

        boolean tituloInvalido = hospedagemDTO.titulo() == null || hospedagemDTO.titulo().isBlank();
        boolean descricaoInvalida = hospedagemDTO.descricao() == null || hospedagemDTO.descricao().isBlank();
        boolean valorDiarioInvalido = hospedagemDTO.valorDiaria() == null || hospedagemDTO.valorDiaria() <= 0;
        boolean comodidadesInvalidas = hospedagemDTO.comodidades() == null || hospedagemDTO.comodidades().isEmpty();

        if (tituloInvalido || descricaoInvalida || valorDiarioInvalido || comodidadesInvalidas) {
            throw new HospedagemBadRequestException("Campos obrigatórios não preenchidos corretamente");
        }
    }
}
