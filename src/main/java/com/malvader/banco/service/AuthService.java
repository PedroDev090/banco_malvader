package com.malvader.banco.service;

import com.malvader.banco.dto.LoginDTO;
import com.malvader.banco.dto.LoginResponseDTO;
import com.malvader.banco.models.*;
import com.malvader.banco.repository.UsuarioRepository;
import com.malvader.banco.repository.FuncionarioRepository;
import com.malvader.banco.repository.ClienteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    /**
     * Autentica um usuário (funcionário ou cliente)
     */
    @Transactional(readOnly = true)
    public LoginResponseDTO autenticar(LoginDTO loginDTO) {

        // Validar entrada
        if (loginDTO.getCpf() == null || loginDTO.getCpf().trim().isEmpty()) {
            return LoginResponseDTO.erro("CPF é obrigatório");
        }

        if (loginDTO.getSenha() == null || loginDTO.getSenha().trim().isEmpty()) {
            return LoginResponseDTO.erro("Senha é obrigatória");
        }

        if (loginDTO.getTipoAcesso() == null || loginDTO.getTipoAcesso().trim().isEmpty()) {
            return LoginResponseDTO.erro("Tipo de acesso é obrigatório");
        }

        String cpf = loginDTO.getCpf().trim();
        String senha = loginDTO.getSenha();
        String tipoAcesso = loginDTO.getTipoAcesso().toUpperCase();

        // Buscar usuário pelo CPF
        Usuario usuario = usuarioRepository.findByCpf(cpf)
                .orElseThrow(() -> new RuntimeException("CPF não encontrado"));

        // Verificar se o tipo de acesso corresponde
        if (!usuario.getTipoUsuario().name().equals(tipoAcesso)) {
            String mensagemErro = String.format(
                    "Este CPF está cadastrado como %s. Selecione o tipo correto.",
                    usuario.getTipoUsuario().name().toLowerCase()
            );
            return LoginResponseDTO.erro(mensagemErro);
        }

        // Verificar senha (MD5 como está no banco)
        String senhaHash = DigestUtils.md5DigestAsHex(senha.getBytes());
        if (!usuario.getSenhaHash().equals(senhaHash)) {
            return LoginResponseDTO.erro("Senha incorreta");
        }

        // Login bem-sucedido - montar resposta
        return construirRespostaLogin(usuario);
    }

    /**
     * Constrói a resposta de login baseada no tipo de usuário
     */
    private LoginResponseDTO construirRespostaLogin(Usuario usuario) {
        String redirectUrl;
        Cargo cargo = null;

        if (usuario.getTipoUsuario() == TipoUsuario.FUNCIONARIO) {
            // Buscar dados do funcionário
            Funcionario funcionario = funcionarioRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Dados de funcionário não encontrados"));

            cargo = funcionario.getCargo();
            redirectUrl = determinarRedirectFuncionario(funcionario);

        } else {
            // Cliente
            clienteRepository.findByUsuario(usuario)
                    .orElseThrow(() -> new RuntimeException("Dados de cliente não encontrados"));

            redirectUrl = "/cliente/dashboard";
        }

        return LoginResponseDTO.sucesso(
                usuario.getTipoUsuario().name(),
                usuario.getNome(),
                usuario.getIdUsuario(),
                cargo,
                redirectUrl
        );
    }

    /**
     * Determina a URL de redirecionamento baseada no cargo do funcionário
     */
    private String determinarRedirectFuncionario(Funcionario funcionario) {
        switch (funcionario.getCargo()) {
            case GERENTE:
                return "/dashboard/funcionario/gerente";
            case ATENDENTE:
                return "/dashboard/funcionario/atendente";
            case ESTAGIARIO:
                return "/dashboard/funcionario/estagiario";
            default:
                return "/dashboard/funcionario";
        }
    }

    /**
     * Verifica se um CPF existe no sistema
     * (usado pelo endpoint /auth/verificar-cpf no AuthController)
     */
    @Transactional(readOnly = true)
    public boolean verificarCpfExistente(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }

    /**
     * Obtém o tipo de usuário para um CPF
     * (usado também pelo /auth/verificar-cpf para pré-selecionar o tipo)
     */
    @Transactional(readOnly = true)
    public String obterTipoUsuarioPorCpf(String cpf) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        return usuarioOpt.map(u -> u.getTipoUsuario().name()).orElse(null);
    }
}
