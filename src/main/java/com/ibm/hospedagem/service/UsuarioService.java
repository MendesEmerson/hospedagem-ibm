package com.ibm.hospedagem.service;

import com.ibm.hospedagem.dto.UsuarioDTO;

public interface UsuarioService {
    UsuarioDTO createUsuario(UsuarioDTO usuarioDTO);
    UsuarioDTO findUsuarioById(Long id);
    UsuarioDTO updateUsuarioById(Long id, UsuarioDTO usuarioDTO);
    void deleteUsuarioById(Long id);
}
