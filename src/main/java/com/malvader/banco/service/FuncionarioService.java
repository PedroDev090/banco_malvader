package com.malvader.banco.service;

import com.malvader.banco.models.Cargo;
import com.malvader.banco.models.Funcionario;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.repository.FuncionarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    /**
     * Busca funcionário pelo usuário
     */
    public Optional<Funcionario> buscarPorUsuario(Usuario usuario) {
        return funcionarioRepository.findByUsuario(usuario);
    }

    /**
     * Busca funcionário pelo ID do usuário
     */
    public Optional<Funcionario> buscarPorIdUsuario(Integer idUsuario) {
        // Primeiro precisaríamos buscar o usuário, então este método seria implementado
        // quando tivermos o UserService
        return Optional.empty();
    }

    /**
     * Busca funcionário com agência e endereço (otimizado)
     */
    public Optional<Funcionario> buscarComAgenciaEEndereco(Integer idFuncionario) {
        return funcionarioRepository.findByIdWithAgenciaAndEndereco(idFuncionario);
    }

    /**
     * Verifica se um funcionário tem permissão de gerente
     */
    public boolean isGerente(Integer idFuncionario) {
        return funcionarioRepository.findById(idFuncionario)
                .map(funcionario -> funcionario.getCargo() == Cargo.GERENTE)
                .orElse(false);
    }

    /**
     * Conta funcionários por agência (para validação de limite)
     */
    public Long contarFuncionariosPorAgencia(Integer idAgencia) {
        return funcionarioRepository.countByAgenciaId(idAgencia);
    }
}