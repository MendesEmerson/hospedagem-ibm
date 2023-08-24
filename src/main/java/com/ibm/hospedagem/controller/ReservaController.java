package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reservas")
@AllArgsConstructor
@CrossOrigin
public class ReservaController {

    @Autowired
    private final ReservaService reservaService;

    @Operation(summary = "Criar uma nova reserva")
    @ApiResponse(responseCode = "201", description = "Criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @PostMapping
    ResponseEntity<ReservaDTO> createHospedagem(@RequestBody ReservaDTO hospedagemDTO) {
        ReservaDTO newHospedagem = reservaService.createHospedagem(hospedagemDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newHospedagem.id()).toUri();
        return ResponseEntity.created(uri).body(newHospedagem);
    }

    @Operation(summary = "Buscar todas as reservas cadastradas")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaDTO.class))))
    @GetMapping
    ResponseEntity<List<ReservaDTO>> findAllHospedagens() {
        List<ReservaDTO> allHospedagens = reservaService.findAll();
        return ResponseEntity.ok(allHospedagens);
    }

    @Operation(summary = "Buscar reservas por Status")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @GetMapping("/status/{status}")
    ResponseEntity<List<ReservaDTO>> findHospedagensByStatus(@PathVariable String status) {
        Status enumStatus = Status.fromValue(status);
        List<ReservaDTO> allHospedagensByStatus = reservaService.findByStatus(enumStatus);
        return ResponseEntity.ok(allHospedagensByStatus);
    }

    @Operation(summary = "Buscar reserva por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @GetMapping("/{id}")
    ResponseEntity<ReservaDTO> findHospedagemById(@PathVariable Long id) {
        ReservaDTO hospedagem = reservaService.findById(id);
        return ResponseEntity.ok(hospedagem);
    }

    @Operation(summary = "Atualizar reserva por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @PutMapping("/{id}")
    ResponseEntity<ReservaDTO> updateHospedagemById(@PathVariable Long id, @RequestBody ReservaDTO hospedagemDTO) {
        ReservaDTO updateHospedagem = reservaService.updateById(id, hospedagemDTO);
        return ResponseEntity.ok(updateHospedagem);
    }

    @Operation(summary = "Deletar reserva por ID (Muda o status para cancelada)")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @DeleteMapping("/{id}/cancelar")
    ResponseEntity<ReservaDTO> deleteHospedagemById(@PathVariable Long id) {
        ReservaDTO hospedagemDelete = reservaService.deleteById(id);
        return ResponseEntity.ok(hospedagemDelete);
    }

    @Operation(summary = "Buscar dias indisponiveis para reserva")
    @GetMapping("/calendario")
    ResponseEntity<List<LocalDate>> findDiasIndisponiveis() {
        List<LocalDate> diasIndisponiveis = reservaService.findDiasIndisponiveis();
        return ResponseEntity.ok(diasIndisponiveis);
    }


}
