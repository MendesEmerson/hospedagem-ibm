package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.model.enums.Status;
import com.ibm.hospedagem.service.HospedagemService;
import com.ibm.hospedagem.service.exception.hospedagemException.HospedagemBadRequestException;
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
import java.util.List;

@RestController
@RequestMapping("/reservas")
@AllArgsConstructor
public class HospedagemController {

    @Autowired
    private final HospedagemService hospedagemService;

    @Operation(summary = "Criar uma nova reserva")
    @ApiResponse(responseCode = "201", description = "Criado", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HospedagemDTO.class)))
    @PostMapping
    ResponseEntity<HospedagemDTO> createHospedagem(@RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO newHospedagem = hospedagemService.createHospedagem(hospedagemDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newHospedagem.getId()).toUri();
        return ResponseEntity.created(uri).body(newHospedagem);
    }

    @Operation(summary = "Buscar todas as reservas cadastradas")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = HospedagemDTO.class))))
    @GetMapping
    ResponseEntity<List<HospedagemDTO>> findAllHospedagens() {
        List<HospedagemDTO> allHospedagens = hospedagemService.findAll();
        return ResponseEntity.ok(allHospedagens);
    }

    @GetMapping("/status/{status}")
    ResponseEntity<List<HospedagemDTO>> findHospedagensByStatus(@PathVariable String status) {
        Status enumStatus = Status.fromValue(status);
        List<HospedagemDTO> allHospedagensByStatus = hospedagemService.findByStatus(enumStatus);
        return ResponseEntity.ok(allHospedagensByStatus);
    }

    @Operation(summary = "Buscar reservar por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HospedagemDTO.class)))
    @GetMapping("/{id}")
    ResponseEntity<HospedagemDTO> findHospedagemById(@PathVariable Long id) {
        HospedagemDTO hospedagem = hospedagemService.findById(id);
        return ResponseEntity.ok(hospedagem);
    }

    @Operation(summary = "Atualizar reserva por ID")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HospedagemDTO.class)))
    @PutMapping("/{id}")
    ResponseEntity<HospedagemDTO> updateHospedagemById(@PathVariable Long id, @RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO updateHospedagem = hospedagemService.updateById(id, hospedagemDTO);
        return ResponseEntity.ok(updateHospedagem);
    }

    @Operation(summary = "Deletar reserva por ID (Muda o status para cancelada)")
    @ApiResponse(responseCode = "200", description = "Sucesso", content = @Content(mediaType = "application/json", schema = @Schema(implementation = HospedagemDTO.class)))
    @DeleteMapping("/{id}/cancelar")
    ResponseEntity<HospedagemDTO> deleteHospedagemById(@PathVariable Long id) {
        HospedagemDTO hospedagemDelete = hospedagemService.deleteById(id);
       return ResponseEntity.ok(hospedagemDelete);
    }


}
