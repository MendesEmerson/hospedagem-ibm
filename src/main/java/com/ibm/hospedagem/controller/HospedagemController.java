package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.service.HospedagemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/hospedagem")
@AllArgsConstructor
public class HospedagemController {

    @Autowired
    private final HospedagemService hospedagemService;

    @PostMapping
    ResponseEntity<HospedagemDTO> createHospedagem(@RequestBody @Valid HospedagemDTO hospedagemDTO) {
        HospedagemDTO newHospedagem = hospedagemService.createHospedagem(hospedagemDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newHospedagem.id()).toUri();
        return ResponseEntity.created(uri).body(newHospedagem);
    }

    @GetMapping("/{id}")
    ResponseEntity<HospedagemDTO> FindHospedegamById(@PathVariable Long id){
        HospedagemDTO hospedagem = hospedagemService.findHospedagemById(id);
        return ResponseEntity.ok(hospedagem);
    }

    @PutMapping("/{id}")
    ResponseEntity<HospedagemDTO> updateHospedagemById(@PathVariable Long id, @RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO hospedagemUpdate = hospedagemService.updateHospedagemById(id, hospedagemDTO);
        return ResponseEntity.ok(hospedagemUpdate);
    }

    @DeleteMapping("/{id}")
    ResponseEntity<Void> deleteHospedagemById(@PathVariable Long id) {
         hospedagemService.deleteById(id);
         return ResponseEntity.noContent().build();
    }

}
