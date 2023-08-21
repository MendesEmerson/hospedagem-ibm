package com.ibm.hospedagem.service.serviceimpl;

import com.ibm.hospedagem.dto.UsuarioDTO;
import com.ibm.hospedagem.model.Usuario;
import com.ibm.hospedagem.repository.UsuarioRepository;
import com.ibm.hospedagem.service.UsuarioService;
import com.ibm.hospedagem.service.exception.usuarioException.UsuarioAlreadyExistException;
import com.ibm.hospedagem.service.exception.usuarioException.UsuarioBadRequestException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {

        verificarCamposObrigatorios(usuarioDTO);

        Usuario newUsuario = usuarioRepository.save(toEntity(usuarioDTO));

        return toDTO(newUsuario);
    }

    @Override
    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioBadRequestException("Usuario com o ID " + id + " Não encontrado"));
        return toDTO(usuario);
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsername(),
                usuario.getPassword(),
                usuario.getHospedagens()
        );
    }

    private Usuario toEntity(UsuarioDTO usuarioDTO) {
        return new Usuario(
                usuarioDTO.getId(),
                usuarioDTO.getUsername(),
                usuarioDTO.getPassword(),
                usuarioDTO.getHospedagens()
        );
    }

    private void verificarCamposObrigatorios(UsuarioDTO usuarioDTO) {
        Usuario findByUsername = usuarioRepository.findByUsername(usuarioDTO.getUsername());

        boolean invalidUsername = usuarioDTO.getUsername() == null || usuarioDTO.getUsername().isBlank();
        boolean invalidPassword = usuarioDTO.getPassword() == null || usuarioDTO.getPassword().isBlank();

        if (findByUsername != null) {
            throw new UsuarioAlreadyExistException("Nome de usuario (" + usuarioDTO.getUsername() + ") indisponivel");
        } else if (invalidUsername) {
            throw new UsuarioBadRequestException("O campo (username) deve ser preenchido");
        } else if (invalidPassword) {
            throw new UsuarioBadRequestException("o campo (password) deve ser preenchido");
        } else if (usuarioDTO.getPassword().length() < 6 || usuarioDTO.getPassword().length() > 16) {
            throw new UsuarioBadRequestException(" o campo (password) deve conter entre 6 e 15 caracteres");
        }
    }
}
