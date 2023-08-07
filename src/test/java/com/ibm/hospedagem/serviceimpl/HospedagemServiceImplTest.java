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
    void testFindAll() {
        Hospedagem hospedagem1 = new Hospedagem();
        Hospedagem hospedagem2 = new Hospedagem();

        when(hospedagemRepository.findAll()).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<HospedagemDTO> result = hospedagemService.findAll();

        assertEquals(2, result.size());
    }

    @Test
    void testFindByStatus_ValidStatus() {
        Hospedagem hospedagem1 = new Hospedagem();
        Hospedagem hospedagem2 = new Hospedagem();
        Status status = Status.CONFIRMADO;

        when(hospedagemRepository.findByStatus(status)).thenReturn(Arrays.asList(hospedagem1, hospedagem2));

        List<HospedagemDTO> result = hospedagemService.findByStatus(status);

        assertEquals(2, result.size());
    }

    @Test
    void testFindByStatus_InvalidStatus() {
        Status status = null;

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.findByStatus(status));
    }

    @Test
    void testFindById_ValidId() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.of(hospedagem));

        HospedagemDTO result = hospedagemService.findById(id);

        assertNotNull(result);
        assertEquals(id, result.getId());
    }

    @Test
    void testFindById_InvalidId() {
        Long id = 1L;

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.findById(id));
    }

    @Test
    void testCreateHospedagem_ValidData() {
        HospedagemDTO hospedagemDTO = new HospedagemDTO(
                1L,
                "Fulano",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(3),
                2,
                Status.CONFIRMADO
        );

        when(hospedagemRepository.save(any())).thenReturn(new Hospedagem());

        HospedagemDTO result = hospedagemService.createHospedagem(hospedagemDTO);

        assertNotNull(result);
        assertEquals(hospedagemDTO, result);
        assertEquals(Status.CONFIRMADO, result.getStatus());
    }

    @Test
    void testCreateHospedagem_MissingData() {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();

        assertThrows(HospedagemBadRequestException.class, () -> hospedagemService.createHospedagem(hospedagemDTO));
    }

    @Test
    void testUpdateById_ValidData() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);

        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setNomeHospede("Novo Nome");
        hospedagemDTO.setDataInicio(LocalDate.now().plusDays(1));
        hospedagemDTO.setDataFim(LocalDate.now().plusDays(3));
        hospedagemDTO.setStatus(Status.CONFIRMADO);
        hospedagemDTO.setQuantidadePessoas(2);

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.of(hospedagem));
        when(hospedagemRepository.save(any())).thenReturn(hospedagem);

        HospedagemDTO result = hospedagemService.updateById(id, hospedagemDTO);

        assertNotNull(result);
        assertEquals(hospedagemDTO.getNomeHospede(), result.getNomeHospede());
    }

    @Test
    void testUpdateById_InvalidId() {
        Long id = 1L;
        HospedagemDTO hospedagemDTO = new HospedagemDTO();

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.updateById(id, hospedagemDTO));
    }

    @Test
    void testDeleteById_ValidId() {
        Long id = 1L;
        Hospedagem hospedagem = new Hospedagem();
        hospedagem.setId(id);

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.of(hospedagem));

        assertDoesNotThrow(() -> hospedagemService.deleteById(id));
    }

    @Test
    void testDeleteById_InvalidId() {
        Long id = 1L;

        when(hospedagemRepository.findById(id)).thenReturn(java.util.Optional.empty());

        assertThrows(HospedagemNotFoundException.class, () -> hospedagemService.deleteById(id));
    }

}
