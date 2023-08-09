package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.service.HospedagemService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class HospedagemControllerTest {

    @Mock
    private HospedagemService hospedagemService;

    @InjectMocks
    private HospedagemController hospedagemController;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(hospedagemController).build();
    }

    @Test
    void testCreateHospedagem() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.createHospedagem(any())).thenReturn(hospedagemDTO);

        mockMvc.perform(post("/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testFindAllHospedagens() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.findAll()).thenReturn(Collections.singletonList(hospedagemDTO));

        mockMvc.perform(get("/reservas"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testFindHospedagensByStatus() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.findByStatus(any())).thenReturn(Collections.singletonList(hospedagemDTO));

        mockMvc.perform(get("/reservas/status/{status}", "CONFIRMADO"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    void testFindHospedagemById() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.findById(any())).thenReturn(hospedagemDTO);

        mockMvc.perform(get("/reservas/{id}", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testUpdateHospedagemById() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.updateById(any(), any())).thenReturn(hospedagemDTO);

        mockMvc.perform(put("/reservas/{id}", 1)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void testDeleteHospedagemById() throws Exception {
        HospedagemDTO hospedagemDTO = new HospedagemDTO();
        hospedagemDTO.setId(1L);

        when(hospedagemService.deleteById(any())).thenReturn(hospedagemDTO);

        mockMvc.perform(delete("/reservas/{id}/cancelar", 1))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1));
    }

}
