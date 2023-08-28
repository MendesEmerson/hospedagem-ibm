package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.dto.UsuarioDTO;
import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.model.Reserva;
import com.ibm.hospedagem.model.Usuario;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.repository.ReservaRepository;
import com.ibm.hospedagem.service.HospedagemService;
import com.ibm.hospedagem.service.ReservaService;
import com.ibm.hospedagem.service.UsuarioService;
import com.ibm.hospedagem.service.exception.reservaException.ReservaBadRequestException;
import com.ibm.hospedagem.service.exception.reservaException.ReservaNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ReservaServiceImpl implements ReservaService {


    @Autowired
    private final ReservaRepository reservaRepository;

    @Autowired
    private final HospedagemService hospedagemService;

    @Autowired
    private final UsuarioService usuarioService;

    @Override
    public List<ReservaDTO> findAll() {
        return reservaRepository.findAll().stream().map(this::toDTO).collect(Collectors.toList());
    }

    @Override
    public List<ReservaDTO> findByStatus(Status status) {

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
    public ReservaDTO createReserva(Long hospedagemId, Long usuarioId, ReservaDTO reserva) {

        validarCamposObrigatorios(reserva);
        var novaReserva = toEntity(reserva);
        verificarConflitosDeDatas(null, hospedagemId, novaReserva.getDataInicio(), novaReserva.getDataFim());

        var hospedagemDTO = hospedagemService.findHospedagemById(hospedagemId);
        //mudar busca de usuario para o context da autenticação assim que implementar security
        var usuarioDTO = usuarioService.findUsuarioById(usuarioId);
        var hospedagem = hospedagemToEntity(hospedagemDTO);
        var usuario = usuarioToEntity(usuarioDTO);

        var valorTotal = calcularValorDaReserva(hospedagem.getValorDiaria(), novaReserva.getDataInicio(), novaReserva.getDataFim());

        novaReserva.setHospedagem(hospedagem);
        novaReserva.setUsuario(usuario);
        novaReserva.setValorTotalReserva(valorTotal);
        novaReserva.setStatus(Status.CONFIRMADO);

        var createReserva = reservaRepository.save(novaReserva);
        return toDTO(createReserva);
    }

    @Override
    public ReservaDTO updateById(Long id, ReservaDTO atualizacaoReserva) {

        validarCamposObrigatorios(atualizacaoReserva);
        var reserva = toEntity(atualizacaoReserva);

        var reservaAtual = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Reserva com id " + id + " não encontrada"));

        var hospedagemId = reservaAtual.getHospedagem().getId();

        verificarConflitosDeDatas(id, hospedagemId, reserva.getDataInicio(), reserva.getDataFim());

        if (reservaAtual.getStatus().equals(Status.CANCELADO)) {
            throw new ReservaBadRequestException("Reserva cancelada, não é possivel fazer alterações em reservas que foram canceladas no sistema");
        }

        if (atualizacaoReserva.status().equals(Status.CANCELADO)) {
            throw new ReservaBadRequestException("Você não pode alterar o status para cancelado, se desejar cancelar sua reservaAtual vá para area de exclusão.");
        }

        reservaAtual.setNomeHospede(reserva.getNomeHospede());
        reservaAtual.setDataInicio(reserva.getDataInicio());
        reservaAtual.setDataFim(reserva.getDataFim());
        reservaAtual.setQuantidadePessoas(reserva.getQuantidadePessoas());
        reservaAtual.setStatus(reserva.getStatus());

        reservaRepository.save(reservaAtual);

        return toDTO(reservaAtual);
    }


    @Override
    public ReservaDTO deleteById(Long id) {
        Reserva reserva = reservaRepository.findById(id)
                .orElseThrow(() -> new ReservaNotFoundException("Impossivel deletar reserva. Reserva com id " + id + " não encontrada"));

        if (reserva.getStatus().equals(Status.CANCELADO)) {
            throw new ReservaBadRequestException("Essa reserva já esta cancelada no sistema");
        }

        reserva.setStatus(Status.CANCELADO);
        reservaRepository.save(reserva);

        return toDTO(reserva);
        //hospedagemRepository.deleteById(reserva.getId());
    }

    private void validarCamposObrigatorios(ReservaDTO reservaDTO) {
        if (reservaDTO.nomeHospede() == null || reservaDTO.nomeHospede().isBlank()) {
            throw new ReservaBadRequestException("Campo [Nome Hospede] não foi preenchido corretamente");
        }
        if (reservaDTO.dataInicio() == null) {
            throw new ReservaBadRequestException("Campo [Data Inicial] não foi preenchido corretamente");
        }
        if (reservaDTO.dataFim() == null) {
            throw new ReservaBadRequestException("Campo [Data Final] não foi preenchido corretamente");
        }
        if (reservaDTO.quantidadePessoas() == null || reservaDTO.quantidadePessoas() <= 0) {
            throw new ReservaBadRequestException("Campo [Quantidade de Pessoas] não foi preenchido corretamente");
        }

        try {
            LocalDate.parse(reservaDTO.dataInicio());
            LocalDate.parse(reservaDTO.dataFim());
        } catch (DateTimeParseException e) {
            throw new ReservaBadRequestException("Campo [Data Inicial] ou [Data Final] não foi preenchido corretamente");
        }
    }

    private void verificarConflitosDeDatas(Long reserva_Id, Long hospedagem_Id, LocalDate dataInicio, LocalDate dataFim) {
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

        var hospedagem = hospedagemService.findHospedagemById(hospedagem_Id);
        List<Reserva> hospedagensConflitantes = hospedagem.reservas();

        for (Reserva reserva : hospedagensConflitantes) {
            if (reserva.getStatus() == Status.CANCELADO) {
                continue;
            }

            LocalDate reservaInicio = reserva.getDataInicio();
            LocalDate reservaFim = reserva.getDataFim();

            if (dataInicio.isBefore(reservaFim) && dataFim.isAfter(reservaInicio)) {
                if (!reserva.getId().equals(reserva_Id)) {
                    throw new ReservaBadRequestException("Conflito de datas. Já existe(m) reserva(s) para o período selecionado.");
                }
            }
        }
    }

    private double calcularValorDaReserva(Double valorDiaria, LocalDate dataInicio, LocalDate dataFim) {
        long diasTotaisDeViagem = ChronoUnit.DAYS.between(dataInicio, dataFim);
        return valorDiaria * diasTotaisDeViagem;
    }

    private ReservaDTO toDTO(Reserva reserva) {
        return new ReservaDTO(
                reserva.getId(),
                reserva.getNomeHospede(),
                reserva.getDataInicio().toString(),
                reserva.getDataFim().toString(),
                reserva.getQuantidadePessoas(),
                reserva.getValorTotalReserva(),
                reserva.getStatus(),
                reserva.getHospedagem(),
                reserva.getUsuario()
        );
    }

    private Reserva toEntity(ReservaDTO reservaDTO) {
        return new Reserva(
                reservaDTO.id(),
                reservaDTO.nomeHospede(),
                LocalDate.parse(reservaDTO.dataInicio()),
                LocalDate.parse(reservaDTO.dataFim()),
                reservaDTO.quantidadePessoas(),
                reservaDTO.valorTotalReserva(),
                reservaDTO.status(),
                reservaDTO.hospedagem(),
                reservaDTO.usuario()
        );
    }

    private Hospedagem hospedagemToEntity(HospedagemDTO hospedagemDTO) {
        return new Hospedagem(
                hospedagemDTO.id(),
                hospedagemDTO.titulo(),
                hospedagemDTO.descricao(),
                hospedagemDTO.valorDiaria(),
                hospedagemDTO.comodidades(),
                hospedagemDTO.reservas()
        );
    }

    private Usuario usuarioToEntity(UsuarioDTO usuarioDTO) {
        return new Usuario(
                usuarioDTO.id(),
                usuarioDTO.usuario(),
                usuarioDTO.senha(),
                usuarioDTO.reservas()
        );
    }

}
