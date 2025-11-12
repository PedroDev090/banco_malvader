package com.malvader.banco.service;

import com.malvader.banco.models.*;
import com.malvader.banco.repository.TransacaoRepository;
import com.malvader.banco.repository.ContaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class TransacaoService {

    @Autowired
    private TransacaoRepository transacaoRepository;

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private SaldoService saldoService; // ← NOVA DEPENDÊNCIA

    // REMOVIDO: private ContaService contaService;

    /**
     * Realizar depósito
     */
    public Transacao realizarDeposito(Integer idContaDestino, BigDecimal valor, String descricao) {
        Optional<Conta> contaOpt = contaRepository.findById(idContaDestino);
        if (contaOpt.isEmpty() || contaOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta destino não encontrada ou inativa");
        }

        validarValorPositivo(valor, "depósito");

        // Criar transação
        Transacao transacao = new Transacao(
                null,
                contaOpt.get(),
                TipoTransacao.DEPOSITO,
                valor,
                descricao != null ? descricao : "Depósito realizado"
        );

        // Atualizar saldo diretamente (sem dependência do ContaService)
        Conta conta = contaOpt.get();
        conta.setSaldo(conta.getSaldo().add(valor));
        contaRepository.save(conta);

        return transacaoRepository.save(transacao);
    }

    /**
     * Realizar saque
     */
    public Transacao realizarSaque(Integer idContaOrigem, BigDecimal valor, String descricao) {
        Optional<Conta> contaOpt = contaRepository.findById(idContaOrigem);
        if (contaOpt.isEmpty() || contaOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta origem não encontrada ou inativa");
        }

        validarValorPositivo(valor, "saque");

        // Validar saldo suficiente usando SaldoService
        if (!saldoService.temSaldoSuficiente(idContaOrigem, valor)) {
            throw new RuntimeException("Saldo insuficiente para realizar o saque");
        }

        // Criar transação
        Transacao transacao = new Transacao(
                contaOpt.get(),
                null,
                TipoTransacao.SAQUE,
                valor,
                descricao != null ? descricao : "Saque realizado"
        );

        // Atualizar saldo diretamente
        Conta conta = contaOpt.get();
        conta.setSaldo(conta.getSaldo().subtract(valor));
        contaRepository.save(conta);

        return transacaoRepository.save(transacao);
    }

    /**
     * Realizar transferência entre contas
     */
    public Transacao realizarTransferencia(Integer idContaOrigem, Integer idContaDestino,
                                           BigDecimal valor, String descricao) {
        // Validar contas
        Optional<Conta> contaOrigemOpt = contaRepository.findById(idContaOrigem);
        Optional<Conta> contaDestinoOpt = contaRepository.findById(idContaDestino);

        if (contaOrigemOpt.isEmpty() || contaOrigemOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta origem não encontrada ou inativa");
        }

        if (contaDestinoOpt.isEmpty() || contaDestinoOpt.get().getStatus() != StatusConta.ATIVA) {
            throw new RuntimeException("Conta destino não encontrada ou inativa");
        }

        validarTransferenciaMesmaConta(idContaOrigem, idContaDestino);
        validarValorPositivo(valor, "transferência");

        // Validar saldo suficiente usando SaldoService
        if (!saldoService.temSaldoSuficiente(idContaOrigem, valor)) {
            throw new RuntimeException("Saldo insuficiente para realizar a transferência");
        }

        // Criar transação
        Transacao transacao = new Transacao(
                contaOrigemOpt.get(),
                contaDestinoOpt.get(),
                TipoTransacao.TRANSFERENCIA,
                valor,
                descricao != null ? descricao : "Transferência entre contas"
        );

        // Atualizar saldos diretamente
        Conta contaOrigem = contaOrigemOpt.get();
        Conta contaDestino = contaDestinoOpt.get();

        contaOrigem.setSaldo(contaOrigem.getSaldo().subtract(valor));
        contaDestino.setSaldo(contaDestino.getSaldo().add(valor));

        contaRepository.save(contaOrigem);
        contaRepository.save(contaDestino);

        return transacaoRepository.save(transacao);
    }

    /**
     * Validar valor positivo
     */
    private void validarValorPositivo(BigDecimal valor, String operacao) {
        if (valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Valor do " + operacao + " deve ser positivo");
        }
    }

    /**
     * Validar transferência para mesma conta
     */
    private void validarTransferenciaMesmaConta(Integer idContaOrigem, Integer idContaDestino) {
        if (idContaOrigem.equals(idContaDestino)) {
            throw new RuntimeException("Não é possível transferir para a mesma conta");
        }
    }

    // Manter os outros métodos (buscarExtrato, aplicarTaxa, aplicarRendimento, etc.)
    // Eles permanecem iguais, apenas removendo a dependência do ContaService

    /**
     * Buscar extrato por período
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarExtrato(Integer idConta, LocalDateTime inicio, LocalDateTime fim) {
        return transacaoRepository.findExtratoPorPeriodo(idConta, inicio, fim);
    }

    /**
     * Buscar últimas transações
     */
    @Transactional(readOnly = true)
    public List<Transacao> buscarUltimasTransacoes(Integer idConta, int limite) {
        return transacaoRepository.findUltimasTransacoes(idConta, limite);
    }

    /**
     * Calcular total movimentado por período
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularTotalMovimentado(LocalDateTime inicio, LocalDateTime fim, TipoTransacao tipo) {
        Double total = transacaoRepository.calcularTotalMovimentadoPorPeriodo(inicio, fim, tipo);
        return BigDecimal.valueOf(total != null ? total : 0.0);
    }

    /**
     * Aplicar taxa
     */
    public Transacao aplicarTaxa(Integer idConta, BigDecimal valorTaxa, String motivo) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        if (contaOpt.isEmpty()) {
            throw new RuntimeException("Conta não encontrada");
        }

        Transacao transacao = new Transacao(
                contaOpt.get(),
                null,
                TipoTransacao.TAXA,
                valorTaxa,
                "Taxa: " + motivo
        );

        Conta conta = contaOpt.get();
        conta.setSaldo(conta.getSaldo().subtract(valorTaxa));
        contaRepository.save(conta);

        return transacaoRepository.save(transacao);
    }

    /**
     * Aplicar rendimento
     */
    public Transacao aplicarRendimento(Integer idConta, BigDecimal valorRendimento, String descricao) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        if (contaOpt.isEmpty()) {
            throw new RuntimeException("Conta não encontrada");
        }

        Transacao transacao = new Transacao(
                null,
                contaOpt.get(),
                TipoTransacao.RENDIMENTO,
                valorRendimento,
                descricao != null ? descricao : "Rendimento aplicado"
        );

        Conta conta = contaOpt.get();
        conta.setSaldo(conta.getSaldo().add(valorRendimento));
        contaRepository.save(conta);

        return transacaoRepository.save(transacao);
    }
}