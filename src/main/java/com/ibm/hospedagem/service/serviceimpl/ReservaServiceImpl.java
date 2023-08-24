package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.model.Reserva;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.repository.ReservaRepository;
import com.ibm.hospedagem.service.ReservaService;
import com.ibm.hospedagem.service.exception.reservaException.ReservaBadRequestException;
import com.ibm.hospedagem.service.exception.reservaException.ReservaNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservaServiceImpl implements ReservaService {

    @Autowired
    private final ReservaRepository reservaRepository;

    @Override
    public List<ReservaDTO> findAll() {
        return reservaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReservaDTO> findByStatus(Status status) {
        if (status == null) {
            throw new ReservaBadRequestException("O valor não pode ser nulo");
        }

        if (status.equals(Status.CONFIRMADO) || status.equals(Status.PENDENTE) || status.equals(Status.CANCELADO)) {
            return reservaRepository.findByStatus(status).stream().map(this::toDTO).toList();
        } else {
            throw new ReservaBadRequestException("Status selecionado invalido (" + status + "), os Status permitidos são: CONFIRMADO, PENDENTE OU DELETADO!");
        }
    }

    @Override
    public List<LocalDate> findDiasIndisponiveis() {
        List<Reserva> hospedagens = reservaRepository.findAll();

        List<LocalDate> diasIndisponiveis = new ArrayList<>();

        for (Reserva hospedagem : hospedagens) {
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
    public ReservaDTO findById(Long id) {
        Reserva hospedagem = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva com id " + id + " não encontrada"));
        return toDTO(hospedagem);
    }

    @Override
    public ReservaDTO createHospedagem(ReservaDTO hospedagemDTO) {

        validarCamposObrigatorios(hospedagemDTO);

        verificarConflitosDeDatas(null, hospedagemDTO.dataInicio(), hospedagemDTO.dataFim());

        Reserva newHospedagem = toEntity(hospedagemDTO);
        newHospedagem.setStatus(Status.CONFIRMADO);

        Reserva savedHospedagem = reservaRepository.save(newHospedagem);
        return toDTO(savedHospedagem);
    }

    @Override
    public ReservaDTO updateById(Long id, ReservaDTO hospedagemDTO) {
        Reserva getHospedagem = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva com id " + id + " não encontrada"));

        if (getHospedagem.getStatus().equals(Status.CANCELADO)) {
            throw new ReservaBadRequestException("Reserva cancelada, não é possivel fazer alterações em reservas que foram canceladas no sistema");
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
                throw new ReservaBadRequestException("Você não pode alterar o status para cancelado, se desejar cancelar sua reserva vá para area de exclusão.");
            }
        }
        if (!hospedagemDTO.quantidadePessoas().equals(getHospedagem.getQuantidadePessoas())) {
            getHospedagem.setQuantidadePessoas(hospedagemDTO.quantidadePessoas());
        }

        reservaRepository.save(getHospedagem);

        return toDTO(getHospedagem);
    }


    @Override
    public ReservaDTO deleteById(Long id) {
        Reserva hospedagem = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Impossivel deletar reserva. Reserva com id " + id + " não encontrada"));

        if (hospedagem.getStatus().equals(Status.CANCELADO)) {
            throw new ReservaBadRequestException("Essa reserva já esta cancelada no sistema");
        }

        hospedagem.setStatus(Status.CANCELADO);
        reservaRepository.save(hospedagem);

        return toDTO(hospedagem);
        //hospedagemRepository.deleteById(hospedagem.getId());
    }

    private ReservaDTO toDTO(Reserva hospedagem) {
        return new ReservaDTO(
                hospedagem.getId(),
                hospedagem.getNomeHospede(),
                hospedagem.getDataInicio(),
                hospedagem.getDataFim(),
                hospedagem.getQuantidadePessoas(),
                hospedagem.getUsuario(),
                hospedagem.getStatus()
        );
    }

    private Reserva toEntity(ReservaDTO hospedagemDTO) {
        return new Reserva(
                hospedagemDTO.id(),
                hospedagemDTO.nomeHospede(),
                hospedagemDTO.dataInicio(),
                hospedagemDTO.dataFim(),
                hospedagemDTO.quantidadePessoas(),
                hospedagemDTO.usuario(),
                hospedagemDTO.status()
        );
    }

    private void validarCamposObrigatorios(ReservaDTO hospedagemDTO) {
        if (hospedagemDTO.nomeHospede() == null || hospedagemDTO.nomeHospede().isBlank()
                || hospedagemDTO.dataInicio() == null
                || hospedagemDTO.dataFim() == null
                || hospedagemDTO.quantidadePessoas() == null || hospedagemDTO.quantidadePessoas() <= 0) {
            throw new ReservaBadRequestException("Campos obrigatórios não preenchidos corretamente");
        }
    }

    private void verificarConflitosDeDatas(Long id, LocalDate dataInicio, LocalDate dataFim) {
        LocalDate dataAtual = LocalDate.now();

        if (dataInicio.isBefore(dataAtual)) {
            throw new ReservaBadRequestException("A data de início não pode ser anterior à data atual.");
        }

        if (dataInicio.isAfter(dataFim)) {
            throw new ReservaBadRequestException("A data de início não pode ser posterior à data de término.");
        }

        if (dataInicio.isEqual(dataFim)) {
            throw new ReservaBadRequestException("É necessário fazer reservas de no mínimo 1 dia.");
        }

        List<Reserva> hospedagensConflitantes = reservaRepository.findAll();

        for (Reserva hospedagem : hospedagensConflitantes) {
            if (hospedagem.getStatus() == Status.CANCELADO) {
                continue;
            }

            LocalDate reservaInicio = hospedagem.getDataInicio();
            LocalDate reservaFim = hospedagem.getDataFim();

            if (dataInicio.isBefore(reservaFim) && dataFim.isAfter(reservaInicio)) {
                if (!hospedagem.getId().equals(id)) {
                    throw new ReservaBadRequestException("Conflito de datas. Já existe(m) reserva(s) para o período selecionado.");
                }
            }
        }
    }


}
