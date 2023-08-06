package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.service.HospedagemService;
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

    @PostMapping
    ResponseEntity<HospedagemDTO> createHospedagem(@RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO newHospedagem = hospedagemService.createHospedagem(hospedagemDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newHospedagem.getId()).toUri();
        return ResponseEntity.created(uri).body(newHospedagem);
    }

    @GetMapping
    ResponseEntity<List<HospedagemDTO>> findAllHospedagens() {
        List<HospedagemDTO> allHospedagens = hospedagemService.findAll();
        return ResponseEntity.ok(allHospedagens);
    }

    @GetMapping("/{id}")
    ResponseEntity<HospedagemDTO> findHospedagemById(@PathVariable Long id) {
        HospedagemDTO hospedagem = hospedagemService.findById(id);
        return ResponseEntity.ok(hospedagem);
    }

    @PutMapping("/{id}")
    ResponseEntity<HospedagemDTO> updateHospedagemById(@PathVariable Long id, @RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO updateHospedagem = hospedagemService.updateById(id, hospedagemDTO);
        return ResponseEntity.ok(updateHospedagem);
    }

    @DeleteMapping("/{id}/cancelar")
    ResponseEntity<Void> deleteHospedagemById(@PathVariable Long id) {
        hospedagemService.deleteById(id);
       return ResponseEntity.noContent().build();
    }


}
