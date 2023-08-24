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
import java.util.ArrayList;
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

        if (status.equals(Status.CONFIRMADO) || status.equals(Status.PENDENTE) || status.equals(Status.CANCELADO)) {
            return hospedagemRepository.findByStatus(status).stream().map(this::toDTO).toList();
        } else {
            throw new HospedagemBadRequestException("Status selecionado invalido (" + status + "), os Status permitidos são: CONFIRMADO, PENDENTE OU DELETADO!");
        }
    }

    @Override
    public List<LocalDate> findDiasIndisponiveis() {
        List<Hospedagem> hospedagens = hospedagemRepository.findAll();

        List<LocalDate> diasIndisponiveis = new ArrayList<>();

        for (Hospedagem hospedagem : hospedagens) {
            if (hospedagem.getStatus() != Status.CANCELADO) {
                LocalDate dataInicio = hospedagem.getDataInicio();
                LocalDate dataFim = hospedagem.getDataFim();

                while (!dataInicio.isAfter(dataFim)) {
                    diasIndisponiveis.add(dataInicio);
                    dataInicio = dataInicio.plusDays(1);
                }
            }
        }

        return diasIndisponiveis;
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

        verificarConflitosDeDatas(null, hospedagemDTO.dataInicio(), hospedagemDTO.dataFim());

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

        LocalDate dataInicioNovaHospedagem = hospedagemDTO.dataInicio();
        LocalDate dataFimNovaHospedagem = hospedagemDTO.dataFim();

        if (!dataInicioNovaHospedagem.equals(getHospedagem.getDataInicio())
                || !dataFimNovaHospedagem.equals(getHospedagem.getDataFim())) {
            verificarConflitosDeDatas(id, dataInicioNovaHospedagem, dataFimNovaHospedagem);
        }

        if (!hospedagemDTO.nomeHospede().equals(getHospedagem.getNomeHospede())) {
            getHospedagem.setNomeHospede(hospedagemDTO.nomeHospede());
        }
        if (!hospedagemDTO.dataInicio().equals(getHospedagem.getDataInicio())) {
            getHospedagem.setDataInicio(hospedagemDTO.dataInicio());
        }
        if (!hospedagemDTO.dataFim().equals(getHospedagem.getDataFim())) {
            getHospedagem.setDataFim(hospedagemDTO.dataFim());
        }
        if (!hospedagemDTO.status().equals(getHospedagem.getStatus())) {
            Status newStatus = hospedagemDTO.status();
            if (newStatus.equals(Status.CONFIRMADO) || newStatus.equals(Status.PENDENTE)) {
                getHospedagem.setStatus(newStatus);
            }
            if (newStatus.equals(Status.CANCELADO)) {
                throw new HospedagemBadRequestException("Você não pode alterar o status para cancelado, se desejar cancelar sua reserva vá para area de exclusão.");
            }
        }
        if (!hospedagemDTO.quantidadePessoas().equals(getHospedagem.getQuantidadePessoas())) {
            getHospedagem.setQuantidadePessoas(hospedagemDTO.quantidadePessoas());
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
                hospedagem.getUsuario(),
                hospedagem.getStatus()
        );
    }

    private Hospedagem toEntity(HospedagemDTO hospedagemDTO) {
        return new Hospedagem(
                hospedagemDTO.id(),
                hospedagemDTO.nomeHospede(),
                hospedagemDTO.dataInicio(),
                hospedagemDTO.dataFim(),
                hospedagemDTO.quantidadePessoas(),
                hospedagemDTO.usuario(),
                hospedagemDTO.status()
        );
    }

    private void validarCamposObrigatorios(HospedagemDTO hospedagemDTO) {
        if (hospedagemDTO.nomeHospede() == null || hospedagemDTO.nomeHospede().isBlank()
                || hospedagemDTO.dataInicio() == null
                || hospedagemDTO.dataFim() == null
                || hospedagemDTO.quantidadePessoas() == null || hospedagemDTO.quantidadePessoas() <= 0) {
            throw new HospedagemBadRequestException("Campos obrigatórios não preenchidos corretamente");
        }
    }

    private void verificarConflitosDeDatas(Long id, LocalDate dataInicio, LocalDate dataFim) {
        LocalDate dataAtual = LocalDate.now();

        if (dataInicio.isBefore(dataAtual)) {
            throw new HospedagemBadRequestException("A data de início não pode ser anterior à data atual.");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new HospedagemBadRequestException("A data de início não pode ser posterior à data de término.");
        }

        if (dataInicio.isEqual(dataFim)) {
            throw new HospedagemBadRequestException("É necessário fazer reservas de no mínimo 1 dia.");
        }

        List<Hospedagem> hospedagensConflitantes = hospedagemRepository.findAll();

        for (Hospedagem hospedagem : hospedagensConflitantes) {
            if (hospedagem.getStatus() == Status.CANCELADO) {
                continue;
            }

            LocalDate reservaInicio = hospedagem.getDataInicio();
            LocalDate reservaFim = hospedagem.getDataFim();

            if (dataInicio.isBefore(reservaFim) && dataFim.isAfter(reservaInicio)) {
                if (!hospedagem.getId().equals(id)) {
                    throw new HospedagemBadRequestException("Conflito de datas. Já existe(m) reserva(s) para o período selecionado.");
                }
            }
        }
    }


}
