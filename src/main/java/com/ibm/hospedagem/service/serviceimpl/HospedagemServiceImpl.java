package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.repository.HospedagemRepository;
import com.ibm.hospedagem.service.HospedagemService;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class HospedagemServiceImpl implements HospedagemService {

    @Autowired
    private final HospedagemRepository hospedagemRepository;

    @Override
    public List<HospedagemDTO> findAll() {
        return hospedagemRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public HospedagemDTO findById(Long id) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Reserva com id " + id + " não encontrada"));
        return toDTO(hospedagem);
    }

    @Override
    public HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO) {

        boolean verifyName = hospedagemDTO.getNomeHospede() == null || hospedagemDTO.getNomeHospede().isBlank();
        boolean verifyDataInicio = hospedagemDTO.getDataInicio() == null;
        boolean verifyDataFinal = hospedagemDTO.getDataFim() == null;
        boolean verifyQuantidadePessoas = hospedagemDTO.getQuantidadePessoas() == null || hospedagemDTO.getQuantidadePessoas() <= 0;

        if(verifyDataFinal) {
            throw new HospedagemBadRequestException("O campo dataFinal deve ser preenchido");
        }
        if(verifyQuantidadePessoas) {
            throw new HospedagemBadRequestException("O campo quantidadePessoas deve possuir um valor maior que 0");
        }
        if(verifyDataInicio) {
            throw new HospedagemBadRequestException("O campo dataInicio deve ser preenchido");
        }
        if(verifyName) {
            throw new HospedagemBadRequestException("O campo nomeHospede deve ser preenchido");
        }

        Hospedagem newHospedagem = toEntity(hospedagemDTO);
        newHospedagem.setStatus("CONFIRMADA");
        Hospedagem saveHospedagem = hospedagemRepository.save(newHospedagem);
        return toDTO(saveHospedagem);
    }

    @Override
    public HospedagemDTO updateById(Long id, HospedagemDTO hospedagemDTO) {
        Hospedagem getHospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Reserva com id " + id + " não encontrada"));

        boolean verifyName = hospedagemDTO.getNomeHospede().equals(getHospedagem.getNomeHospede());
        boolean verifyDataInicio = hospedagemDTO.getDataInicio().equals(getHospedagem.getDataInicio());
        boolean verifyDataFinal = hospedagemDTO.getDataFim().equals(getHospedagem.getDataFim());
        boolean verifyStatus = hospedagemDTO.getStatus().equals(getHospedagem.getStatus());
        boolean verifyQuantidadePessoas = hospedagemDTO.getQuantidadePessoas().equals(getHospedagem.getQuantidadePessoas());

        if (verifyName && verifyStatus && verifyDataFinal && verifyDataInicio && verifyQuantidadePessoas){
            throw new HospedagemBadRequestException("Pelo menos um atributo deve ser modificado");
        }

        if (!verifyName) {
            getHospedagem.setNomeHospede(hospedagemDTO.getNomeHospede());
        }
        if (!verifyDataInicio) {
            getHospedagem.setDataInicio(hospedagemDTO.getDataInicio());
        }
        if(verifyDataFinal) {
            getHospedagem.setDataFim(hospedagemDTO.getDataFim());
        }
        if(!verifyStatus) {
            getHospedagem.setStatus(hospedagemDTO.getStatus());
        }
        if(!verifyQuantidadePessoas){
            getHospedagem.setQuantidadePessoas(hospedagemDTO.getQuantidadePessoas());
        }

        hospedagemRepository.save(getHospedagem);

        return toDTO(getHospedagem);
    }

    @Override
    public void deleteById(Long id) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Impossivel deletar reserva. Reserva com id " + id + " não encontrada"));
        hospedagemRepository.deleteById(hospedagem.getId());
    }

    private HospedagemDTO toDTO(Hospedagem hospedagem) {
        return new HospedagemDTO(
                hospedagem.getId(),
                hospedagem.getNomeHospede(),
                hospedagem.getDataInicio(),
                hospedagem.getDataFim(),
                hospedagem.getQuantidadePessoas(),
                hospedagem.getStatus()
        );
    }

    private Hospedagem toEntity(HospedagemDTO hospedagemDTO) {
        return new Hospedagem(
                hospedagemDTO.getId(),
                hospedagemDTO.getNomeHospede(),
                hospedagemDTO.getDataInicio(),
                hospedagemDTO.getDataFim(),
                hospedagemDTO.getQuantidadePessoas(),
                hospedagemDTO.getStatus()
        );
    }
}
