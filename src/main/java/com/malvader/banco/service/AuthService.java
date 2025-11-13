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
@Transactional
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
    public LoginResponseDTO autenticar(LoginDTO loginDTO) {
        try {
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
            Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);

            if (usuarioOpt.isEmpty()) {
                return LoginResponseDTO.erro("CPF não encontrado");
            }

            Usuario usuario = usuarioOpt.get();

            // Verificar se o tipo de acesso corresponde
            if (!usuario.getTipoUsuario().name().equals(tipoAcesso)) {
                String mensagemErro = String.format("Este CPF está cadastrado como %s. Selecione o tipo correto.",
                        usuario.getTipoUsuario().name().toLowerCase());
                return LoginResponseDTO.erro(mensagemErro);
            }

            // Verificar senha (MD5 como está no banco)
            String senhaHash = DigestUtils.md5DigestAsHex(senha.getBytes());
            if (!usuario.getSenhaHash().equals(senhaHash)) {
                return LoginResponseDTO.erro("Senha incorreta");
            }

            // Login bem-sucedido - montar resposta
            return construirRespostaLogin(usuario);

        } catch (Exception e) {
            // Log do erro (em produção usar Logger)
            System.err.println("Erro durante autenticação: " + e.getMessage());
            e.printStackTrace();

            return LoginResponseDTO.erro("Erro interno no servidor. Tente novamente.");
        }
    }

    /**
     * Constrói a resposta de login baseada no tipo de usuário
     */
    private LoginResponseDTO construirRespostaLogin(Usuario usuario) {
        String redirectUrl;
        Cargo cargo = null;

        if (usuario.getTipoUsuario() == TipoUsuario.FUNCIONARIO) {
            // Buscar dados do funcionário
            Optional<Funcionario> funcionarioOpt = funcionarioRepository.findByUsuario(usuario);
            if (funcionarioOpt.isEmpty()) {
                return LoginResponseDTO.erro("Dados de funcionário não encontrados");
            }

            Funcionario funcionario = funcionarioOpt.get();
            cargo = funcionario.getCargo();
            redirectUrl = determinarRedirectFuncionario(funcionario);

        } else {
            // Cliente
            Optional<Cliente> clienteOpt = clienteRepository.findByUsuario(usuario);
            if (clienteOpt.isEmpty()) {
                return LoginResponseDTO.erro("Dados de cliente não encontrados");
            }

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
     */
    public boolean verificarCpfExistente(String cpf) {
        return usuarioRepository.existsByCpf(cpf);
    }

    /**
     * Obtém o tipo de usuário para um CPF (para preencher automaticamente o formulário)
     */
    public String obterTipoUsuarioPorCpf(String cpf) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        return usuarioOpt.map(usuario -> usuario.getTipoUsuario().name()).orElse(null);
    }
}