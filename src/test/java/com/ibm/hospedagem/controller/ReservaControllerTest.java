//package com.ibm.hospedagem.controller;
//
//import com.ibm.hospedagem.dto.ReservaDTO;
//import com.ibm.hospedagem.model.Hospedagem;
//import com.ibm.hospedagem.model.Reserva;
//import com.ibm.hospedagem.model.Usuario;
//import com.ibm.hospedagem.model.enums.Status;
//import com.ibm.hospedagem.service.ReservaService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.http.MediaType;
//import org.springframework.test.web.servlet.MockMvc;
//import org.springframework.test.web.servlet.setup.MockMvcBuilders;
//
//import java.time.LocalDate;
//import java.util.ArrayList;
//import java.util.Collections;
//
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.when;
//import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
//
//class ReservaControllerTest {
//
//    @Mock
//    private ReservaService reservaService;
//
//    @InjectMocks
//    private ReservaController reservaController;
//
//    private MockMvc mockMvc;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        mockMvc = MockMvcBuilders.standaloneSetup(reservaController).build();
//    }
//
//    Usuario usuario = new Usuario(
//            1L,
//            "Emerson",
//            "123456"
//
//    );
//
//    Hospedagem hospedagem = new Hospedagem(
//            1l,
//            "Titulo",
//            "descrição",
//            157.00,
//            new ArrayList<String>(),
//            new ArrayList<Reserva>()
//    );
//
//    ReservaDTO hospedagemDTO = new ReservaDTO(
//            1L,
//            "Novo Nome",
//            LocalDate.now().plusDays(1),
//            LocalDate.now().plusDays(3),
//            2,
//            usuario,
//            Status.CONFIRMADO,
//            hospedagem
//    );
//
//    @Test
//    void testCreateHospedagem() throws Exception {
//
//        when(reservaService.createReserva(any())).thenReturn(hospedagemDTO);
//
//        mockMvc.perform(post("/reservas")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isCreated())
//                .andExpect(header().exists("Location"))
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testFindAllHospedagens() throws Exception {
//
//        when(reservaService.findAll()).thenReturn(Collections.singletonList(hospedagemDTO));
//
//        mockMvc.perform(get("/reservas"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void testFindHospedagensByStatus() throws Exception {
//
//        when(reservaService.findByStatus(any())).thenReturn(Collections.singletonList(hospedagemDTO));
//
//        mockMvc.perform(get("/reservas/status/{status}", "CONFIRMADO"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$[0].id").value(1));
//    }
//
//    @Test
//    void testFindHospedagemById() throws Exception {
//
//        when(reservaService.findById(any())).thenReturn(hospedagemDTO);
//
//        mockMvc.perform(get("/reservas/{id}", 1))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testUpdateHospedagemById() throws Exception {
//
//        when(reservaService.updateById(any(), any())).thenReturn(hospedagemDTO);
//
//        mockMvc.perform(put("/reservas/{id}", 1)
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content("{}"))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//    @Test
//    void testDeleteHospedagemById() throws Exception {
//
//        when(reservaService.deleteById(any())).thenReturn(hospedagemDTO);
//
//        mockMvc.perform(delete("/reservas/{id}/cancelar", 1))
//                .andExpect(status().isOk())
//                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
//                .andExpect(jsonPath("$.id").value(1));
//    }
//
//}
