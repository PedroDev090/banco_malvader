package com.malvader.banco.service;

import com.malvader.banco.models.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OperacaoService {

    @Autowired
    private TransacaoService transacaoService;

    @Autowired
    private ContaService contaService;

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private SaldoService saldoService; // ← NOVA DEPENDÊNCIA

    /**
     * Realizar operação bancária completa com validações
     */
    public Transacao realizarOperacao(Integer idContaOrigem, Integer idContaDestino,
                                      TipoTransacao tipo, BigDecimal valor, String descricao) {

        validarValorOperacao(valor);

        try {
            switch (tipo) {
                case DEPOSITO:
                    validarDeposito(idContaDestino);
                    return transacaoService.realizarDeposito(idContaDestino, valor, descricao);

                case SAQUE:
                    validarSaque(idContaOrigem, valor);
                    return transacaoService.realizarSaque(idContaOrigem, valor, descricao);

                case TRANSFERENCIA:
                    validarTransferencia(idContaOrigem, idContaDestino, valor);
                    return transacaoService.realizarTransferencia(idContaOrigem, idContaDestino, valor, descricao);

                default:
                    throw new RuntimeException("Tipo de operação não suportado: " + tipo);
            }
        } catch (Exception e) {
            throw new RuntimeException("Erro ao realizar operação: " + e.getMessage());
        }
    }

    /**
     * Validar valor da operação
     */
    private void validarValorOperacao(BigDecimal valor) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor da operação deve ser positivo");
        }

        if (valor.compareTo(new BigDecimal("100000")) > 0) {
            throw new RuntimeException("Valor máximo por operação é R$ 100.000,00");
        }
    }

    /**
     * Validar depósito
     */
    private void validarDeposito(Integer idContaDestino) {
        if (idContaDestino == null) {
            throw new RuntimeException("Conta destino é obrigatória para depósito");
        }

        // Validar se conta destino existe e está ativa
        if (!contaService.buscarCompleta(idContaDestino)
                .map(conta -> conta.getStatus() == StatusConta.ATIVA)
                .orElse(false)) {
            throw new RuntimeException("Conta destino não encontrada ou inativa");
        }
    }

    /**
     * Validar saque
     */
    private void validarSaque(Integer idContaOrigem, BigDecimal valor) {
        if (idContaOrigem == null) {
            throw new RuntimeException("Conta origem é obrigatória para saque");
        }

        // Validar saldo usando SaldoService
        if (!saldoService.temSaldoSuficiente(idContaOrigem, valor)) {
            throw new RuntimeException("Saldo insuficiente para realizar o saque");
        }

        // Validar se conta origem existe e está ativa
        if (!contaService.buscarCompleta(idContaOrigem)
                .map(conta -> conta.getStatus() == StatusConta.ATIVA)
                .orElse(false)) {
            throw new RuntimeException("Conta origem não encontrada ou inativa");
        }
    }

    /**
     * Validar transferência
     */
    private void validarTransferencia(Integer idContaOrigem, Integer idContaDestino, BigDecimal valor) {
        if (idContaOrigem == null || idContaDestino == null) {
            throw new RuntimeException("Conta origem e destino são obrigatórias para transferência");
        }

        if (idContaOrigem.equals(idContaDestino)) {
            throw new RuntimeException("Não é possível transferir para a mesma conta");
        }

        // Validar saldo usando SaldoService
        if (!saldoService.temSaldoSuficiente(idContaOrigem, valor)) {
            throw new RuntimeException("Saldo insuficiente para realizar a transferência");
        }

        // Validar contas
        Optional<Conta> contaOrigemOpt = contaService.buscarCompleta(idContaOrigem);
        Optional<Conta> contaDestinoOpt = contaService.buscarCompleta(idContaDestino);

        if (contaOrigemOpt.isEmpty() || contaOrigemOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta origem não encontrada ou inativa");
        }

        if (contaDestinoOpt.isEmpty() || contaDestinoOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta destino não encontrada ou inativa");
        }
    }

    /**
     * Consultar saldo da conta
     */
    @Transactional(readOnly = true)
    public BigDecimal consultarSaldo(Integer idConta) {
        return contaService.buscarCompleta(idConta)
                .map(Conta::getSaldo)
                .orElseThrow(() -> new RuntimeException("Conta não encontrada"));
    }

    /**
     * Consultar saldo disponível (incluindo limite)
     */
    @Transactional(readOnly = true)
    public BigDecimal consultarSaldoDisponivel(Integer idConta) {
        return saldoService.calcularSaldoDisponivel(idConta);
    }

    /**
     * Consultar extrato simplificado (últimos 10 dias)
     */
    @Transactional(readOnly = true)
    public List<Transacao> consultarExtratoSimplificado(Integer idConta) {
        LocalDateTime fim = LocalDateTime.now();
        LocalDateTime inicio = fim.minusDays(10);
        return transacaoService.buscarExtrato(idConta, inicio, fim);
    }

    /**
     * Consultar extrato por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> consultarExtratoPeriodo(Integer idConta, LocalDateTime inicio, LocalDateTime fim) {
        validarPeriodoExtrato(inicio, fim);
        return transacaoService.buscarExtrato(idConta, inicio, fim);
    }

    /**
     * Validar período do extrato
     */
    private void validarPeriodoExtrato(LocalDateTime inicio, LocalDateTime fim) {
        if (inicio.until(fim, java.time.temporal.ChronoUnit.DAYS) > 90) {
            throw new RuntimeException("Período máximo para extrato é 90 dias");
        }

        if (inicio.isAfter(fim)) {
            throw new RuntimeException("Data início não pode ser após data fim");
        }
    }

    /**
     * Verificar limites da conta
     */
    @Transactional(readOnly = true)
    public String verificarLimites(Integer idConta) {
        return contaService.buscarCompleta(idConta)
                .map(conta -> {
                    StringBuilder limites = new StringBuilder();
                    BigDecimal saldoDisponivel = saldoService.calcularSaldoDisponivel(idConta);

                    limites.append(String.format("Saldo atual: R$ %.2f\n", conta.getSaldo()));
                    limites.append(String.format("Saldo disponível: R$ %.2f\n", saldoDisponivel));

                    if (conta.getTipoConta() == TipoConta.CORRENTE) {
                        Optional<ContaCorrente> ccOpt = contaService.buscarContaCorrente(idConta);
                        if (ccOpt.isPresent()) {
                            limites.append(String.format("Limite: R$ %.2f\n", ccOpt.get().getLimite()));
                        }
                    }

                    limites.append(String.format("Status: %s", conta.getStatus()));
                    return limites.toString();
                })
                .orElse("Conta não encontrada");
    }

    /**
     * Realizar transferência PIX (simplificado)
     */
    public Transacao realizarPix(Integer idContaOrigem, String chavePix, BigDecimal valor, String descricao) {
        // Em um sistema real, aqui buscaria a conta pela chave PIX
        // Por enquanto, vamos simular encontrando uma conta qualquer
        List<Conta> contas = contaService.buscarContasAtivasPorCliente(1); // Cliente exemplo

        if (contas.isEmpty()) {
            throw new RuntimeException("Conta destino não encontrada para a chave PIX: " + chavePix);
        }

        Integer idContaDestino = contas.get(0).getIdConta();

        // Validar transferência antes de executar
        validarTransferencia(idContaOrigem, idContaDestino, valor);

        return transacaoService.realizarTransferencia(idContaOrigem, idContaDestino, valor,
                descricao != null ? descricao : "PIX para " + chavePix);
    }

    /**
     * Aplicar rendimento automático na poupança
     */
    public void aplicarRendimentoPoupanca() {
        // Buscar todas as contas poupança ativas
        List<Conta> contasPoupanca = contaService.buscarPorTipo(TipoConta.POUPANCA)
                .stream()
                .filter(conta -> conta.getStatus() == StatusConta.ATIVA)
                .toList();

        for (Conta conta : contasPoupanca) {
            // Calcular rendimento (0.5% ao mês, simplificado)
            BigDecimal rendimento = conta.getSaldo().multiply(new BigDecimal("0.005"));

            if (rendimento.compareTo(BigDecimal.ZERO) > 0) {
                transacaoService.aplicarRendimento(
                        conta.getIdConta(),
                        rendimento,
                        "Rendimento poupança"
                );
            }
        }
    }

    /**
     * Verificar se operação é permitida para o cliente
     */
    @Transactional(readOnly = true)
    public boolean operacaoPermitida(Integer idCliente, TipoTransacao tipo, BigDecimal valor) {
        Optional<Cliente> clienteOpt = clienteService.buscarPorId(idCliente);
        if (clienteOpt.isEmpty()) {
            return false;
        }

        Cliente cliente = clienteOpt.get();

        // Clientes com score baixo têm restrições
        if (cliente.getScoreCredito() < 30.0) {
            return valor.compareTo(new BigDecimal("1000")) <= 0; // Limite de R$ 1.000,00
        }

        // Validar limites específicos por tipo de operação
        return saldoService.validarLimiteOperacao(idCliente, valor, tipo.name());
    }

    /**
     * Consultar estatísticas do cliente
     */
    @Transactional(readOnly = true)
    public String consultarEstatisticasCliente(Integer idCliente) {
        Optional<Cliente> clienteOpt = clienteService.buscarPorId(idCliente);
        if (clienteOpt.isEmpty()) {
            return "Cliente não encontrado";
        }

        Cliente cliente = clienteOpt.get();
        List<Conta> contas = contaService.buscarContasAtivasPorCliente(idCliente);
        BigDecimal patrimonio = contaService.calcularPatrimonioCliente(idCliente);
        BigDecimal saldoTotal = contas.stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return String.format(
                "Cliente: %s | Score: %.1f | Contas: %d | Patrimônio: R$ %.2f | Saldo Total: R$ %.2f",
                cliente.getUsuario().getNome(),
                cliente.getScoreCredito(),
                contas.size(),
                patrimonio,
                saldoTotal
        );
    }
}