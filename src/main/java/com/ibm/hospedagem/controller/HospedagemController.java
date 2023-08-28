package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.HospedagemDTO;
import com.ibm.hospedagem.service.HospedagemService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/hospedagem")
@AllArgsConstructor
public class HospedagemController {

    @Autowired
    private final HospedagemService hospedagemService;

    @PostMapping
    ResponseEntity<HospedagemDTO> createHospedagem(@RequestBody HospedagemDTO hospedagemDTO) {
        HospedagemDTO newHospedagem = hospedagemService.createHospedagem(hospedagemDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newHospedagem.id()).toUri();
        return ResponseEntity.created(uri).body(newHospedagem);
    }

}
