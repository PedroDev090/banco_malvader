package com.malvader.banco.service;

import com.malvader.banco.models.TipoUsuario;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    /**
     * Busca usuário por ID
     */
    public Optional<Usuario> buscarPorId(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario);
    }

    /**
     * Busca usuário por CPF
     */
    public Optional<Usuario> buscarPorCpf(String cpf) {
        return usuarioRepository.findByCpf(cpf);
    }

    /**
     * Busca usuário com endereços (otimizado)
     */
    public Optional<Usuario> buscarComEnderecos(Integer idUsuario) {
        return usuarioRepository.findByIdWithEnderecos(idUsuario);
    }

    /**
     * Verifica se um usuário é funcionário
     */
    public boolean isFuncionario(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> usuario.getTipoUsuario() == TipoUsuario.FUNCIONARIO)
                .orElse(false);
    }

    /**
     * Verifica se um usuário é cliente
     */
    public boolean isCliente(Integer idUsuario) {
        return usuarioRepository.findById(idUsuario)
                .map(usuario -> usuario.getTipoUsuario() == TipoUsuario.CLIENTE)
                .orElse(false);
    }
}