package com.ibm.hospedagem.serviceimpl;

import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.model.Reserva;
import com.ibm.hospedagem.model.Usuario;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.repository.ReservaRepository;
import com.ibm.hospedagem.service.exception.reservaException.ReservaBadRequestException;
import com.ibm.hospedagem.service.exception.reservaException.ReservaNotFoundException;
import com.ibm.hospedagem.service.serviceimpl.ReservaServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ReservaServiceImplTest {

    @Mock
    private ReservaRepository reservaRepository;

    @InjectMocks
    private ReservaServiceImpl hospedagemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    Usuario usuario = new Usuario(
            1L,
            "Emerson",
            "123456"

    );

    @Test
    void encontrarTodasAsHospedagens() {
        Reserva hospedagem1 = new Reserva();
        Reserva hospedagem2 = new Reserva();

        when(reservaRepository.findAll()).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<ReservaDTO> resultado = hospedagemService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    void encontrarHospedagensPorStatus_Valido() {
        Reserva hospedagem1 = new Reserva();
        Reserva hospedagem2 = new Reserva();
        Status status = Status.CONFIRMADO;

        when(reservaRepository.findByStatus(status)).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<ReservaDTO> resultado = hospedagemService.findByStatus(status);

        assertEquals(2, resultado.size());
    }

    @Test
    void excecaoAoEncontrarHospedagensPorStatus_Invalido() {
        Status status = null;

        assertThrows(ReservaBadRequestException.class, () -> hospedagemService.findByStatus(status));
    }

    @Test
    void encontrarHospedagemPorId_Valido() {
        Long id = 1L;
        Reserva hospedagem = new Reserva();
        hospedagem.setId(id);

        when(reservaRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        ReservaDTO resultado = hospedagemService.findById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.id());
    }

    @Test
    void excecaoAoEncontrarHospedagemPorId_Invalido() {
        Long id = 1L;

        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> hospedagemService.findById(id));
    }

    @Test
    void criarHospedagem_ComDadosValidos() {

        var hospedagemDTO = new ReservaDTO(
                1L,
                "Fulano",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                Status.CONFIRMADO,

                usuario
        );

        Reserva hospedagem = new Reserva(
                hospedagemDTO.id(),
                hospedagemDTO.nomeHospede(),
                hospedagemDTO.dataInicio(),
                hospedagemDTO.dataFim(),
                hospedagemDTO.quantidadePessoas(),
                hospedagemDTO.status(),
                hospedagemDTO.hospedagem(),
                hospedagemDTO.usuario()

                );

        when(reservaRepository.save(any())).thenReturn(hospedagem);

        ReservaDTO resultado = hospedagemService.createReserva(hospedagemDTO);

        assertNotNull(resultado);
        assertEquals(hospedagemDTO, resultado);
        assertEquals(Status.CONFIRMADO, resultado.status());
    }

    @Test
    void excecaoAoCriarHospedagem_ComDadosFaltantes() {
        ReservaDTO hospedagemDTO = new ReservaDTO(
                null, null,null, null, null, null, null, null
        );

        assertThrows(ReservaBadRequestException.class, () -> hospedagemService.createReserva(hospedagemDTO));
    }

    @Test
    void atualizarHospedagem_ComDadosValidos() {
        Long id = 1L;
        Reserva hospedagem = new Reserva();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CONFIRMADO);

        ReservaDTO hospedagemDTO = new ReservaDTO(
                1L,
                "Novo Nome",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                usuario,
                Status.CONFIRMADO
        );

        when(reservaRepository.findById(id)).thenReturn(Optional.of(hospedagem));
        when(reservaRepository.save(any())).thenReturn(hospedagem);

        ReservaDTO resultado = hospedagemService.updateById(id, hospedagemDTO);

        assertNotNull(resultado);
        assertEquals(hospedagemDTO.nomeHospede(), resultado.nomeHospede());
    }

    @Test
    void excecaoAoAtualizarHospedagem_ComIdInvalido() {
        Long id = 1L;
        ReservaDTO hospedagemDTO = new ReservaDTO(
                null,
                "Novo Nome",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                usuario,
                Status.CONFIRMADO
        );

        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> hospedagemService.updateById(id, hospedagemDTO));
    }

    @Test
    void excecaoAoAtualizarHospedagem_ComStatusCancelado() {
        Long id = 1L;
        Reserva hospedagem = new Reserva();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CANCELADO);

        ReservaDTO hospedagemDTO = new ReservaDTO(
                1L,
                "Novo Nome",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                usuario,
                Status.CANCELADO
        );

        when(reservaRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertThrows(ReservaBadRequestException.class, () -> hospedagemService.updateById(id, hospedagemDTO));
    }

    @Test
    void excluirHospedagem_ComIdValido() {
        Long id = 1L;
        Reserva hospedagem = new Reserva();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CONFIRMADO);

        when(reservaRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertDoesNotThrow(() -> hospedagemService.deleteById(id));
    }

    @Test
    void excecaoAoExcluirHospedagem_ComIdInvalido() {
        Long id = 1L;

        when(reservaRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(ReservaNotFoundException.class, () -> hospedagemService.deleteById(id));
    }

    @Test
    void excecaoAoExcluirHospedagem_ComStatusCancelado() {
        Long id = 1L;
        Reserva hospedagem = new Reserva();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CANCELADO);

        when(reservaRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertThrows(ReservaBadRequestException.class, () -> hospedagemService.deleteById(id));
    }
}
