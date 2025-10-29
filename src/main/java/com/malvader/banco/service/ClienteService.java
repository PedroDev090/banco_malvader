package com.malvader.banco.service;

import com.malvader.banco.models.Cliente;
import com.malvader.banco.models.Usuario;
import com.malvader.banco.repository.ClienteRepository;
import com.malvader.banco.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ContaService contaService;

    @Autowired
    private TransacaoService transacaoService;

    /**
     * Buscar cliente por ID
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorId(Integer idCliente) {
        return clienteRepository.findById(idCliente);
    }

    /**
     * Buscar cliente por CPF
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorCpf(String cpf) {
        return clienteRepository.findByUsuarioCpf(cpf);
    }

    /**
     * Buscar cliente com dados completos
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarCompleto(Integer idCliente) {
        return clienteRepository.findByIdWithUsuarioCompleto(idCliente);
    }

    /**
     * Buscar todos os clientes
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarTodos() {
        return clienteRepository.findAll();
    }

    /**
     * Buscar clientes com bom score
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarComBomScore() {
        return clienteRepository.findByScoreCreditoGreaterThanEqual(70.0);
    }

    /**
     * Buscar clientes inadimplentes
     */
    @Transactional(readOnly = true)
    public List<Cliente> buscarInadimplentes() {
        return clienteRepository.findByScoreCreditoLessThan(30.0);
    }

    /**
     * Calcular score de crédito do cliente
     */
    public void calcularScoreCredito(Integer idCliente) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();

            // Calcular patrimônio total
            BigDecimal patrimonio = contaService.calcularPatrimonioCliente(idCliente);

            // Buscar total movimentado (últimos 3 meses)
            // BigDecimal movimentacao = transacaoService.calcularTotalMovimentado(...);

            // Fórmula simplificada do score
            double score = Math.min(100.0, patrimonio.doubleValue() / 1000.0);

            cliente.setScoreCredito(score);
            clienteRepository.save(cliente);
        }
    }

    /**
     * Atualizar score de crédito
     */
    public void atualizarScoreCredito(Integer idCliente, Double novoScore) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            cliente.setScoreCredito(Math.min(100.0, Math.max(0.0, novoScore)));
            clienteRepository.save(cliente);
        }
    }

    /**
     * Verificar se cliente existe
     */
    @Transactional(readOnly = true)
    public boolean existeCliente(Integer idCliente) {
        return clienteRepository.existsById(idCliente);
    }

    // Adicionar este método no ClienteService que já existe:

    /**
     * Buscar cliente por ID com usuário
     */
    @Transactional(readOnly = true)
    public Optional<Cliente> buscarPorIdComUsuario(Integer idCliente) {
        return clienteRepository.findById(idCliente)
                .map(cliente -> {
                    // Force load do usuário
                    cliente.getUsuario().getNome();
                    return cliente;
                });
    }

    /**
     * Obter estatísticas do cliente
     */
    @Transactional(readOnly = true)
    public String obterEstatisticasCliente(Integer idCliente) {
        Optional<Cliente> clienteOpt = clienteRepository.findById(idCliente);
        if (clienteOpt.isPresent()) {
            Cliente cliente = clienteOpt.get();
            List<com.malvader.banco.models.Conta> contas = contaService.buscarContasAtivasPorCliente(idCliente);
            BigDecimal patrimonio = contaService.calcularPatrimonioCliente(idCliente);

            return String.format(
                    "Cliente: %s | Score: %.1f | Contas: %d | Patrimônio: R$ %.2f",
                    cliente.getUsuario().getNome(),
                    cliente.getScoreCredito(),
                    contas.size(),
                    patrimonio
            );
        }
        return "Cliente não encontrado";
    }
}