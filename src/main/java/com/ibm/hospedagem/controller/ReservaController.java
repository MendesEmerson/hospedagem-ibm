package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.ReservaDTO;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.service.ReservaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/reserva")
@AllArgsConstructor
@CrossOrigin
public class ReservaController {

    @Autowired
    private final ReservaService reservaService;

    @Operation(summary = "Criar uma nova reserva")
    @ApiResponse(responseCode = "201", description = "Criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @PostMapping("/hospedagem/{id}/{userId}")
    ResponseEntity<ReservaDTO> createHospedagem(
            @RequestBody @Valid ReservaDTO reservaDTO,
            @PathVariable(name = "id") Long hospedagemId,
            @PathVariable(name = "userId") Long usuarioId
    ) {
        ReservaDTO newReserva = reservaService.createReserva(hospedagemId, usuarioId, reservaDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newReserva.id()).toUri();
        return ResponseEntity.created(uri).body(newReserva);
    }

    @Operation(summary = "Buscar todas as reservas cadastradas")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = ReservaDTO.class))))
    @GetMapping
    ResponseEntity<List<ReservaDTO>> findAllReservas() {
        List<ReservaDTO> allReservas = reservaService.findAll();
        return ResponseEntity.ok(allReservas);
    }

    @Operation(summary = "Buscar reservas por Status")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @GetMapping("/status/{status}")
    ResponseEntity<List<ReservaDTO>> findReservaByStatus(@PathVariable String status) {
        Status enumStatus = Status.fromValue(status);
        List<ReservaDTO> allReservaByStatus = reservaService.findByStatus(enumStatus);
        return ResponseEntity.ok(allReservaByStatus);
    }

    @Operation(summary = "Buscar reserva por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @GetMapping("/{id}")
    ResponseEntity<ReservaDTO> findReservaById(@PathVariable Long id) {
        ReservaDTO reserva = reservaService.findById(id);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Atualizar reserva por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @PutMapping("/{id}")
    ResponseEntity<ReservaDTO> updateReservaById(@PathVariable Long id, @RequestBody ReservaDTO reservaDTO) {
        ReservaDTO updateHospedagem = reservaService.updateById(id, reservaDTO);
        return ResponseEntity.ok(updateHospedagem);
    }

    @Operation(summary = "Deletar reserva por ID (Muda o status para cancelada)")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ReservaDTO.class)))
    @DeleteMapping("/{id}/cancelar")
    ResponseEntity<ReservaDTO> deleteReservaById(@PathVariable Long id) {
        ReservaDTO reserva = reservaService.deleteById(id);
        return ResponseEntity.ok(reserva);
    }

    @Operation(summary = "Buscar dias indisponiveis para reserva")
    @GetMapping("/calendario")
    ResponseEntity<List<LocalDate>> findDiasIndisponiveis() {
        List<LocalDate> diasIndisponiveis = reservaService.findDiasIndisponiveis();
        return ResponseEntity.ok(diasIndisponiveis);
    }


}
