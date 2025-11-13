package com.malvader.banco.service;

import com.malvader.banco.models.*;
import com.malvader.banco.repository.ContaRepository;
import com.malvader.banco.repository.ContaCorrenteRepository;
import com.malvader.banco.repository.ContaPoupancaRepository;
import com.malvader.banco.repository.ContaInvestimentoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ContaService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    @Autowired
    private ContaPoupancaRepository contaPoupancaRepository;

    @Autowired
    private ContaInvestimentoRepository contaInvestimentoRepository;

    /**
     * Buscar conta por número
     */
    @Transactional(readOnly = true)
    public Optional<Conta> buscarPorNumero(String numeroConta) {
        return contaRepository.findByNumeroConta(numeroConta);
    }

    /**
     * Buscar conta com todas as relações
     */
    @Transactional(readOnly = true)
    public Optional<Conta> buscarCompleta(Integer idConta) {
        return contaRepository.findByIdCompleta(idConta);
    }

    /**
     * Buscar contas por cliente
     */
    @Transactional(readOnly = true)
    public List<Conta> buscarPorCliente(Integer idCliente) {
        return contaRepository.findByClienteIdCliente(idCliente);
    }

    /**
     * Buscar contas ativas por cliente
     */
    @Transactional(readOnly = true)
    public List<Conta> buscarContasAtivasPorCliente(Integer idCliente) {
        return contaRepository.findByClienteIdClienteAndStatus(idCliente, StatusConta.ATIVA);
    }

    /**
     * Verificar se conta existe e está ativa
     */
    @Transactional(readOnly = true)
    public boolean contaAtivaExiste(String numeroConta) {
        Optional<Conta> conta = contaRepository.findByNumeroConta(numeroConta);
        return conta.isPresent() && conta.get().getStatus() == StatusConta.ATIVA;
    }

    /**
     * Criar conta corrente
     */
    public ContaCorrente criarContaCorrente(Conta conta, BigDecimal limite, LocalDate dataVencimento, BigDecimal taxaManutencao) {
        // Salvar conta base primeiro
        Conta contaSalva = contaRepository.save(conta);

        // Criar conta corrente
        ContaCorrente contaCorrente = new ContaCorrente(contaSalva, limite, dataVencimento, taxaManutencao);
        return contaCorrenteRepository.save(contaCorrente);
    }

    /**
     * Criar conta poupança
     */
    public ContaPoupanca criarContaPoupanca(Conta conta, BigDecimal taxaRendimento) {
        Conta contaSalva = contaRepository.save(conta);
        ContaPoupanca contaPoupanca = new ContaPoupanca(contaSalva, taxaRendimento);
        return contaPoupancaRepository.save(contaPoupanca);
    }

    /**
     * Criar conta investimento
     */
    public ContaInvestimento criarContaInvestimento(Conta conta, PerfilRisco perfilRisco,
                                                    BigDecimal valorMinimo, BigDecimal taxaRendimentoBase) {
        Conta contaSalva = contaRepository.save(conta);
        ContaInvestimento contaInvestimento = new ContaInvestimento(contaSalva, perfilRisco, valorMinimo, taxaRendimentoBase);
        return contaInvestimentoRepository.save(contaInvestimento);
    }

    /**
     * Bloquear conta
     */
    public boolean bloquearConta(Integer idConta, String motivo) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        if (contaOpt.isPresent() && contaOpt.get().getStatus() == StatusConta.ATIVA) {
            Conta conta = contaOpt.get();
            conta.setStatus(StatusConta.BLOQUEADA);
            contaRepository.save(conta);

            // Registrar na auditoria
            // auditoriaService.registrarBloqueio(conta, motivo);
            return true;
        }
        return false;
    }

    /**
     * Desbloquear conta
     */
    public boolean desbloquearConta(Integer idConta) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        if (contaOpt.isPresent() && contaOpt.get().getStatus() == StatusConta.BLOQUEADA) {
            Conta conta = contaOpt.get();
            conta.setStatus(StatusConta.ATIVA);
            contaRepository.save(conta);
            return true;
        }
        return false;
    }

    /**
     * Buscar contas inadimplentes
     */
    @Transactional(readOnly = true)
    public List<Conta> buscarInadimplentes() {
        return contaRepository.findContasComSaldoNegativo();
    }

    /**
     * Calcular patrimônio total do cliente
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularPatrimonioCliente(Integer idCliente) {
        List<Conta> contas = contaRepository.findByClienteIdClienteAndStatus(idCliente, StatusConta.ATIVA);
        return contas.stream()
                .map(Conta::getSaldo)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Buscar conta corrente por ID da conta
     */
    @Transactional(readOnly = true)
    public Optional<ContaCorrente> buscarContaCorrente(Integer idConta) {
        return contaCorrenteRepository.findByContaIdConta(idConta);
    }

    /**
     * Buscar conta poupança por ID da conta
     */
    @Transactional(readOnly = true)
    public Optional<ContaPoupanca> buscarContaPoupanca(Integer idConta) {
        return contaPoupancaRepository.findByContaIdConta(idConta);
    }

    /**
     * Buscar conta investimento por ID da conta
     */
    @Transactional(readOnly = true)
    public Optional<ContaInvestimento> buscarContaInvestimento(Integer idConta) {
        return contaInvestimentoRepository.findByContaIdConta(idConta);
    }

    /**
     * Verificar se conta pertence ao cliente
     */
    @Transactional(readOnly = true)
    public boolean contaPertenceAoCliente(Integer idConta, Integer idCliente) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        return contaOpt.isPresent() && contaOpt.get().getCliente().getIdCliente().equals(idCliente);
    }

    /**
     * Buscar todas as contas por tipo
     */
    @Transactional(readOnly = true)
    public List<Conta> buscarPorTipo(TipoConta tipoConta) {
        return contaRepository.findByTipoConta(tipoConta);
    }

    /**
     * Buscar contas por status
     */
    @Transactional(readOnly = true)
    public List<Conta> buscarPorStatus(StatusConta status) {
        return contaRepository.findByStatus(status);
    }

    /**
     * Contar total de contas por tipo e status
     */
    @Transactional(readOnly = true)
    public Long contarPorTipoEStatus(TipoConta tipo, StatusConta status) {
        return contaRepository.countByTipoAndStatus(tipo, status);
    }
}