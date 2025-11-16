package com.malvader.banco.service;

import com.malvader.banco.models.*;
import com.malvader.banco.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FuncionarioService {

    @Autowired
    private FuncionarioRepository funcionarioRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private AgenciaRepository agenciaRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private TransacaoRepository transacaoRepository;

    /**
     * Cadastrar novo funcionário
     */
    public Funcionario cadastrarFuncionario(String nome, String cpf, LocalDate dataNascimento,
                                            String telefone, String endereco, Cargo cargo,
                                            Integer idAgencia, String senha) {

        // Verificar se CPF já existe
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new RuntimeException("CPF já cadastrado no sistema");
        }

        // Verificar limite de funcionários por agência
        Long totalFuncionarios = funcionarioRepository.countByAgenciaId(idAgencia);
        if (totalFuncionarios >= 20) {
            throw new RuntimeException("Limite de 20 funcionários por agência atingido");
        }

        // Buscar agência
        Agencia agencia = agenciaRepository.findById(idAgencia)
                .orElseThrow(() -> new RuntimeException("Agência não encontrada"));

        // Criar usuário
        Usuario usuario = new Usuario(nome, cpf, dataNascimento, telefone,
                TipoUsuario.FUNCIONARIO, DigestUtils.md5DigestAsHex(senha.getBytes()));
        usuario = usuarioRepository.save(usuario);

        // Gerar código do funcionário
        String codigoFuncionario = "FUNC" + String.format("%04d", usuario.getIdUsuario());

        // Criar funcionário
        Funcionario funcionario = new Funcionario(usuario, agencia, codigoFuncionario, cargo);
        return funcionarioRepository.save(funcionario);
    }

    /**
     * Buscar funcionário por ID
     */
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarPorId(Integer idFuncionario) {
        return funcionarioRepository.findById(idFuncionario);
    }

    /**
     * Buscar todos os funcionários
     */
    @Transactional(readOnly = true)
    public List<Funcionario> buscarTodos() {
        return funcionarioRepository.findAll();
    }

    /**
     * Buscar funcionários por agência
     */
    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorAgencia(Integer idAgencia) {
        return funcionarioRepository.findByAgenciaIdAgencia(idAgencia);
    }

    /**
     * Buscar funcionários por cargo
     */
    @Transactional(readOnly = true)
    public List<Funcionario> buscarPorCargo(Cargo cargo) {
        return funcionarioRepository.findByCargo(cargo);
    }

    /**
     * Atualizar cargo do funcionário
     */
    public Funcionario atualizarCargo(Integer idFuncionario, Cargo novoCargo) {
        Funcionario funcionario = funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        funcionario.setCargo(novoCargo);
        return funcionarioRepository.save(funcionario);
    }

    /**
     * Verificar se funcionário é gerente
     */
    @Transactional(readOnly = true)
    public boolean isGerente(Integer idFuncionario) {
        return funcionarioRepository.findById(idFuncionario)
                .map(func -> func.getCargo() == Cargo.GERENTE)
                .orElse(false);
    }

    /**
     * Verificar se funcionário pode realizar operação
     */
    @Transactional(readOnly = true)
    public boolean podeRealizarOperacao(Integer idFuncionario, String tipoOperacao) {
        Optional<Funcionario> funcionarioOpt = funcionarioRepository.findById(idFuncionario);
        if (funcionarioOpt.isEmpty()) return false;

        Funcionario funcionario = funcionarioOpt.get();

        switch (tipoOperacao) {
            case "ABRIR_CONTA":
                return funcionario.getCargo() == Cargo.ATENDENTE ||
                        funcionario.getCargo() == Cargo.GERENTE;

            case "CADASTRAR_FUNCIONARIO":
                return funcionario.getCargo() == Cargo.GERENTE;

            case "CONSULTAR_DADOS":
                return true; // Todos podem consultar

            case "ENCERRAR_CONTA":
                return funcionario.getCargo() == Cargo.GERENTE;

            case "RELATORIOS_AVANCADOS":
                return funcionario.getCargo() == Cargo.GERENTE;

            default:
                return false;
        }
    }
    /**
     * Buscar funcionário por código e, opcionalmente, por cargo (pode ser null).
     * Usado na tela de "Consultar Dados" na aba Funcionário.
     */
    @Transactional(readOnly = true)
    public Optional<Funcionario> buscarPorCodigoECargo(String codigoFuncionario, String cargoStr) {

        if (cargoStr == null || cargoStr.isBlank()) {
            // Busca somente pelo código
            return funcionarioRepository.findByCodigoFuncionario(codigoFuncionario);
        }

        Cargo cargo;
        try {
            // converte string para enum (ATENDENTE, ESTAGIARIO, GERENTE)
            cargo = Cargo.valueOf(cargoStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            // cargo inválido → considera que não encontrou
            return Optional.empty();
        }

        return funcionarioRepository.findByCodigoFuncionarioAndCargo(codigoFuncionario, cargo);
    }

    /**
     * Quantidade de contas associadas ao funcionário.
     * Aqui reaproveitamos o obterEstatisticas(...) que você já tem.
     */
    @Transactional(readOnly = true)
    public Integer contarContasAbertas(Funcionario funcionario) {
        EstatisticasFuncionarioDTO dto = obterEstatisticas(funcionario.getIdFuncionario());
        return dto.getContasAbertas();
    }

    /**
     * Desempenho do funcionário em forma de texto (média movimentada).
     */
    @Transactional(readOnly = true)
    public String calcularDesempenho(Funcionario funcionario) {
        EstatisticasFuncionarioDTO dto = obterEstatisticas(funcionario.getIdFuncionario());
        BigDecimal media = dto.getMediaMovimentacao();
        if (media == null) {
            return "R$ 0,00";
        }
        // se quiser, depois formata bonitinho com NumberFormat
        return "R$ " + media;
    }

    /**
     * Endereço principal do funcionário.
     * Pega o primeiro endereço da lista de endereços do usuário (se existir).
     */
    @Transactional(readOnly = true)
    public String buscarEnderecoPrincipal(Funcionario funcionario) {
        if (funcionario.getUsuario() == null ||
                funcionario.getUsuario().getEnderecos() == null ||
                funcionario.getUsuario().getEnderecos().isEmpty()) {
            return "";
        }

        // usa o primeiro endereço como "principal"
        var end = funcionario.getUsuario().getEnderecos().get(0);

        // como não vimos a classe EnderecoUsuario, por segurança usamos toString()
        return end.toString();  // depois você pode montar "Rua X, 123 - Cidade/UF"
    }

    /**
     * Obter estatísticas do funcionário
     */
    @Transactional(readOnly = true)
    public EstatisticasFuncionarioDTO obterEstatisticas(Integer idFuncionario) {
        Funcionario funcionario = funcionarioRepository.findById(idFuncionario)
                .orElseThrow(() -> new RuntimeException("Funcionário não encontrado"));

        // Buscar contas abertas pelo funcionário
        List<Conta> contasAbertas = contaRepository.findByClienteIdCliente(idFuncionario); // Simplificado

        // Calcular média de valores movimentados
        BigDecimal totalMovimentado = BigDecimal.valueOf(transacaoRepository.calcularTotalMovimentadoPorPeriodo(
                LocalDateTime.now().minusDays(30), LocalDateTime.now(), null));

        return new EstatisticasFuncionarioDTO(
                funcionario.getUsuario().getNome(),
                contasAbertas.size(),
                totalMovimentado != null ? totalMovimentado : BigDecimal.ZERO,
                funcionario.getCargo()
        );
    }

    // DTO para estatísticas
    public static class EstatisticasFuncionarioDTO {
        private String nome;
        private Integer contasAbertas;
        private BigDecimal mediaMovimentacao;
        private Cargo cargo;

        public EstatisticasFuncionarioDTO(String nome, Integer contasAbertas,
                                          BigDecimal mediaMovimentacao, Cargo cargo) {
            this.nome = nome;
            this.contasAbertas = contasAbertas;
            this.mediaMovimentacao = mediaMovimentacao;
            this.cargo = cargo;
        }

        // Getters
        public String getNome() { return nome; }
        public Integer getContasAbertas() { return contasAbertas; }
        public BigDecimal getMediaMovimentacao() { return mediaMovimentacao; }
        public Cargo getCargo() { return cargo; }
    }
}