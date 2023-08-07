package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.repository.HospedagemRepository;
import com.ibm.hospedagem.service.HospedagemService;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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
    public List<HospedagemDTO> findByStatus(Status status) {
        if (status == null) {
            throw new HospedagemBadRequestException("O valor não pode ser nulo");
        }

        if(status.equals(Status.CONFIRMADO) || status.equals(Status.PENDENTE) || status.equals(Status.CANCELADO)) {
            return hospedagemRepository.findByStatus(status).stream().map(this::toDTO).toList();
        } else {
            throw new HospedagemBadRequestException("Status selecionado invalido (" + status + "), os Status permitidos são: CONFIRMADO, PENDENTE OU DELETADO!");
        }
    }

    @Override
    public HospedagemDTO findById(Long id) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Reserva com id " + id + " não encontrada"));
        return toDTO(hospedagem);
    }

    @Override
    public HospedagemDTO createHospedagem(HospedagemDTO hospedagemDTO) {

        validarCamposObrigatorios(hospedagemDTO);

        verificarConflitosDeDatas(null, hospedagemDTO.getDataInicio(), hospedagemDTO.getDataFim());

        Hospedagem newHospedagem = toEntity(hospedagemDTO);
        newHospedagem.setStatus(Status.CONFIRMADO);

        Hospedagem savedHospedagem = hospedagemRepository.save(newHospedagem);
        return toDTO(savedHospedagem);
    }

    @Override
    public HospedagemDTO updateById(Long id, HospedagemDTO hospedagemDTO) {
        Hospedagem getHospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Reserva com id " + id + " não encontrada"));

        if (getHospedagem.getStatus().equals(Status.CANCELADO)) {
            throw new HospedagemBadRequestException("Reserva cancelada, não é possivel fazer alterações em reservas que foram canceladas no sistema");
        }

        validarCamposObrigatorios(hospedagemDTO);

        LocalDate dataInicioNovaHospedagem = hospedagemDTO.getDataInicio();
        LocalDate dataFimNovaHospedagem = hospedagemDTO.getDataFim();

        if (!dataInicioNovaHospedagem.equals(getHospedagem.getDataInicio())
                || !dataFimNovaHospedagem.equals(getHospedagem.getDataFim())) {
            verificarConflitosDeDatas(id, dataInicioNovaHospedagem, dataFimNovaHospedagem);
        }

        if (!hospedagemDTO.getNomeHospede().equals(getHospedagem.getNomeHospede())) {
            getHospedagem.setNomeHospede(hospedagemDTO.getNomeHospede());
        }
        if (!hospedagemDTO.getDataInicio().equals(getHospedagem.getDataInicio())) {
            getHospedagem.setDataInicio(hospedagemDTO.getDataInicio());
        }
        if (!hospedagemDTO.getDataFim().equals(getHospedagem.getDataFim())) {
            getHospedagem.setDataFim(hospedagemDTO.getDataFim());
        }
        if (!hospedagemDTO.getStatus().equals(getHospedagem.getStatus())) {
            Status newStatus = hospedagemDTO.getStatus();
            if (newStatus.equals(Status.CONFIRMADO) || newStatus.equals(Status.PENDENTE)) {
                getHospedagem.setStatus(newStatus);
            }
            if (newStatus.equals(Status.CANCELADO)){
                throw new HospedagemBadRequestException("Você não pode alterar o status para cancelado, se desejar cancelar sua reserva vá para area de exclusão.");
            }
        }
        if (!hospedagemDTO.getQuantidadePessoas().equals(getHospedagem.getQuantidadePessoas())) {
            getHospedagem.setQuantidadePessoas(hospedagemDTO.getQuantidadePessoas());
        }

        hospedagemRepository.save(getHospedagem);

        return toDTO(getHospedagem);
    }



    @Override
    public HospedagemDTO deleteById(Long id) {
        Hospedagem hospedagem = hospedagemRepository.findById(id)
                .orElseThrow(() -> new HospedagemNotFoundException("Impossivel deletar reserva. Reserva com id " + id + " não encontrada"));

        if (hospedagem.getStatus().equals(Status.CANCELADO)) {
            throw new HospedagemBadRequestException("Essa reserva já esta cancelada no sistema");
        }

        hospedagem.setStatus(Status.CANCELADO);
        hospedagemRepository.save(hospedagem);

        return toDTO(hospedagem);
        //hospedagemRepository.deleteById(hospedagem.getId());
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

    private void validarCamposObrigatorios(HospedagemDTO hospedagemDTO) {
        if (hospedagemDTO.getNomeHospede() == null || hospedagemDTO.getNomeHospede().isBlank()
                || hospedagemDTO.getDataInicio() == null
                || hospedagemDTO.getDataFim() == null
                || hospedagemDTO.getQuantidadePessoas() == null || hospedagemDTO.getQuantidadePessoas() <= 0) {
            throw new HospedagemBadRequestException("Campos obrigatórios não preenchidos corretamente");
        }
    }

    private void verificarConflitosDeDatas(Long id, LocalDate dataInicio, LocalDate dataFim) {
        if (dataInicio.isAfter(dataFim)) {
            throw new HospedagemBadRequestException("A data de início não pode ser posterior à data de término.");
        }

        if (dataInicio.isEqual(dataFim)) {
            throw new HospedagemBadRequestException("é necessario fazer reservas de no minimo 1 dia");
        }

        List<Hospedagem> hospedagensConflitantes = hospedagemRepository.findByDataInicioBetweenOrDataFimBetween(
                dataInicio, dataFim, dataInicio, dataFim);

        hospedagensConflitantes.removeIf(hospedagemConflitante -> hospedagemConflitante.getId().equals(id));

        for (Hospedagem hospedagemConflitante : hospedagensConflitantes) {
            if (hospedagemConflitante.getStatus() != Status.CANCELADO) {
                throw new HospedagemBadRequestException("Conflito de datas. Já existe(m) reserva(s) para o período selecionado.");
            }
        }
    }


}
