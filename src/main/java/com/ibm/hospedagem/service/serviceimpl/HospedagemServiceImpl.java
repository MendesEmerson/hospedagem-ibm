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
        var hospedagem = toEntity(hospedagemDTO);

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

    @Override
    public HospedagemDTO updateHospedagemById(Long id, HospedagemDTO hospedagemDTO) {
        verificarCampos(hospedagemDTO);
        var hospedagem = toEntity(hospedagemDTO);

        var hospedagemAtual = hospedagemRepository.findById(id)
                .orElseThrow(()-> new HospedagemNotFoundException("Hospedagem não encontrada!"));

        hospedagemAtual.setTitulo(hospedagem.getTitulo());
        hospedagemAtual.setDescricao(hospedagem.getDescricao());
        hospedagemAtual.setValorDiaria(hospedagem.getValorDiaria());

        hospedagemRepository.save(hospedagemAtual);

        return toDTO(hospedagemAtual);
    }

    @Override
    public void deleteById(Long id) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(()-> new HospedagemNotFoundException("Hospedagem não encontrada"));
        hospedagemRepository.deleteById(hospedagem.getId());
    }


    private void verificarCampos(HospedagemDTO hospedagemDTO) {

        boolean tituloInvalido = hospedagemDTO.titulo() == null || hospedagemDTO.titulo().isBlank();
        boolean descricaoInvalida = hospedagemDTO.descricao() == null || hospedagemDTO.descricao().isBlank();
        boolean valorDiarioInvalido = hospedagemDTO.valorDiaria() == null || hospedagemDTO.valorDiaria() <= 0;
        boolean comodidadesInvalidas = hospedagemDTO.comodidades() == null || hospedagemDTO.comodidades().isEmpty();

        if (tituloInvalido) {
            throw new HospedagemBadRequestException("Campo [Titulo] não foi preenchido corretamente");
        }
        if (descricaoInvalida) {
            throw new HospedagemBadRequestException("Campo [Descrição] não foi preenchido corretamente");
        }
        if (valorDiarioInvalido) {
            throw new HospedagemBadRequestException("Campo [Valor diário] não foi preenchido corretamente");
        }
        if (comodidadesInvalidas) {
            throw new HospedagemBadRequestException("A lista [Comodidades] não foi preenchido corretamente");
        }
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
}
