package com.ibm.hospedagem.controller;

import com.ibm.hospedagem.dto.UsuarioDTO;
import com.ibm.hospedagem.service.UsuarioService;
import org.springframework.web.bind.annotation.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/usuario")
@AllArgsConstructor
public class UsuarioController {

    @Autowired
    private final UsuarioService usuarioService;

    @GetMapping("/{id}")
    ResponseEntity<UsuarioDTO> findById(@PathVariable Long id) {
        UsuarioDTO findUsuario = usuarioService.findUsuarioById(id);
        return ResponseEntity.ok(findUsuario);
    }

    @PostMapping
    ResponseEntity<UsuarioDTO> createUsuario(@RequestBody UsuarioDTO usuarioDTO) {
        UsuarioDTO newUsuario = usuarioService.createUsuario(usuarioDTO);
        URI uri = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
                .buildAndExpand(newUsuario.id()).toUri();
        return ResponseEntity.created(uri).build();
    }
}
