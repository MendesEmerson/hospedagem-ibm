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

import java.util.ArrayList;

@Service
@AllArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private final UsuarioRepository usuarioRepository;

    @Override
    public UsuarioDTO createUsuario(UsuarioDTO usuarioDTO) {

        verificarCamposObrigatorios(usuarioDTO);
        Usuario usuario = toEntity(usuarioDTO);
        usuario.setReservas(new ArrayList<>());

        Usuario newUsuario = usuarioRepository.save(usuario);

        return toDTO(newUsuario);
    }

    @Override
    public UsuarioDTO findUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioBadRequestException("Usuario com o ID " + id + " Não encontrado"));
        return toDTO(usuario);
    }

    @Override
    public UsuarioDTO updateUsuarioById(Long id, UsuarioDTO usuarioDTO) {
        verificarCamposObrigatorios(usuarioDTO);
        Usuario atualizarUsuario = toEntity(usuarioDTO);

        Usuario usuarioAtual = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioBadRequestException("Usuario com o ID " + id + " Não encontrado"));

        usuarioAtual.setUsuario(atualizarUsuario.getUsuario());
        usuarioAtual.setSenha(atualizarUsuario.getSenha());

        usuarioRepository.save(usuarioAtual);

        return toDTO(usuarioAtual);
    }

    @Override
    public void deleteUsuarioById(Long id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new UsuarioBadRequestException("Usuario com o ID " + id + " Não encontrado"));
        usuarioRepository.deleteById(usuario.getId());
    }

    private void verificarCamposObrigatorios(UsuarioDTO usuarioDTO) {
        Usuario findByUsername = usuarioRepository.findByUsuario(usuarioDTO.usuario());

        boolean invalidUsername = usuarioDTO.usuario() == null || usuarioDTO.usuario().isBlank();
        boolean invalidPassword = usuarioDTO.senha() == null || usuarioDTO.senha().isBlank();

        if (findByUsername != null) {
            throw new UsuarioAlreadyExistException("Nome de usuario (" + usuarioDTO.usuario() + ") indisponivel");
        } else if (invalidUsername) {
            throw new UsuarioBadRequestException("O campo (usuario) deve ser preenchido");
        } else if (invalidPassword) {
            throw new UsuarioBadRequestException("o campo (senha) deve ser preenchido");
        } else if (usuarioDTO.senha().length() < 6 || usuarioDTO.senha().length() > 16) {
            throw new UsuarioBadRequestException(" o campo (senha) deve conter entre 6 e 15 caracteres");
        }
    }

    private UsuarioDTO toDTO(Usuario usuario) {
        return new UsuarioDTO(
                usuario.getId(),
                usuario.getUsuario(),
                usuario.getSenha(),
                usuario.getReservas()
        );
    }

    private Usuario toEntity(UsuarioDTO usuarioDTO) {
        return new Usuario(
                usuarioDTO.id(),
                usuarioDTO.usuario(),
                usuarioDTO.senha(),
                usuarioDTO.reservas()
        );
    }
}
