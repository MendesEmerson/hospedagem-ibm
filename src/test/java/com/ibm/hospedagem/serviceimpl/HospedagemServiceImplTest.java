package com.ibm.hospedagem.serviceimpl;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.Hospedagem;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.repository.HospedagemRepository;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemNotFoundException;
import com.ibm.hospedagem.service.serviceimpl.HospedagemServiceImpl;
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


class HospedagemServiceImplTest {

    @Mock
    private HospedagemRepository hospedagemRepository;

    @InjectMocks
    private HospedagemServiceImpl hospedagemService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void encontrarTodasAsHospedagens() {
        Hospedagem hospedagem1 = new Hospedagem();
        Hospedagem hospedagem2 = new Hospedagem();

        when(hospedagemRepository.findAll()).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<HospedagemDTO> resultado = hospedagemService.findAll();

        assertEquals(2, resultado.size());
    }

    @Test
    void encontrarHospedagensPorStatus_Valido() {
        Hospedagem hospedagem1 = new Hospedagem();
        Hospedagem hospedagem2 = new Hospedagem();
        Status status = Status.CONFIRMADO;

        when(hospedagemRepository.findByStatus(status)).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<HospedagemDTO> resultado = hospedagemService.findByStatus(status);

        assertEquals(2, resultado.size());
    }

    @Test
    void excecaoAoEncontrarHospedagensPorStatus_Invalido() {
        Status status = null;

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.findByStatus(status));
    }

    @Test
    void encontrarHospedagemPorId_Valido() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);

        when(hospedagemRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        HospedagemDTO resultado = hospedagemService.findById(id);

        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
    }

    @Test
    void excecaoAoEncontrarHospedagemPorId_Invalido() {
        Long id = 1L;

        when(hospedagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.findById(id));
    }

    @Test
    void criarHospedagem_ComDadosValidos() {
        HospedagemDTO hospedagemDTO = new HospedagemDTO(
                1L,
                "Fulano",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                Status.CONFIRMADO
        );

        Hospedagem hospedagem = new Hospedagem(
                hospedagemDTO.getId(),
                hospedagemDTO.getNomeHospede(),
                hospedagemDTO.getDataInicio(),
                hospedagemDTO.getDataFim(),
                hospedagemDTO.getQuantidadePessoas(),
                hospedagemDTO.getStatus()
        );

        when(hospedagemRepository.save(any())).thenReturn(hospedagem);

        HospedagemDTO resultado = hospedagemService.createHospedagem(hospedagemDTO);

        assertNotNull(resultado);
        assertEquals(hospedagemDTO, resultado);
        assertEquals(Status.CONFIRMADO, resultado.getStatus());
    }

    @Test
    void excecaoAoCriarHospedagem_ComDadosFaltantes() {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.createHospedagem(hospedagemDTO));
    }

    @Test
    void atualizarHospedagem_ComDadosValidos() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CONFIRMADO);

        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setNomeHospede("Novo Nome");
        hospedagemDTO.setDataInicio(LocalDate.now().plusDays(1));
        hospedagemDTO.setDataFim(LocalDate.now().plusDays(3));
        hospedagemDTO.setStatus(Status.CONFIRMADO);
        hospedagemDTO.setQuantidadePessoas(2);

        when(hospedagemRepository.findById(id)).thenReturn(Optional.of(hospedagem));
        when(hospedagemRepository.save(any())).thenReturn(hospedagem);

        HospedagemDTO resultado = hospedagemService.updateById(id, hospedagemDTO);

        assertNotNull(resultado);
        assertEquals(hospedagemDTO.getNomeHospede(), resultado.getNomeHospede());
    }

    @Test
    void excecaoAoAtualizarHospedagem_ComIdInvalido() {
        Long id = 1L;
        HospedagemDTO hospedagemDTO = new HospedagemDTO();

        when(hospedagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.updateById(id, hospedagemDTO));
    }

    @Test
    void excecaoAoAtualizarHospedagem_ComStatusCancelado() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CANCELADO);

        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setStatus(Status.CONFIRMADO);

        when(hospedagemRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.updateById(id, hospedagemDTO));
    }

    @Test
    void excluirHospedagem_ComIdValido() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CONFIRMADO);

        when(hospedagemRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertDoesNotThrow(() -> hospedagemService.deleteById(id));
    }

    @Test
    void excecaoAoExcluirHospedagem_ComIdInvalido() {
        Long id = 1L;

        when(hospedagemRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.deleteById(id));
    }

    @Test
    void excecaoAoExcluirHospedagem_ComStatusCancelado() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);
        hospedagem.setStatus(Status.CANCELADO);

        when(hospedagemRepository.findById(id)).thenReturn(Optional.of(hospedagem));

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.deleteById(id));
    }
}
