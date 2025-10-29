package com.malvader.banco.service;

import com.malvader.banco.models.Conta;
import com.malvader.banco.models.ContaCorrente;
import com.malvader.banco.models.TipoConta;
import com.malvader.banco.repository.ContaRepository;
import com.malvader.banco.repository.ContaCorrenteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@Transactional
public class SaldoService {

    @Autowired
    private ContaRepository contaRepository;

    @Autowired
    private ContaCorrenteRepository contaCorrenteRepository;

    /**
     * Calcular saldo disponível (incluindo limite para conta corrente)
     */
    @Transactional(readOnly = true)
    public BigDecimal calcularSaldoDisponivel(Integer idConta) {
        Optional<Conta> contaOpt = contaRepository.findById(idConta);
        if (contaOpt.isEmpty()) {
            throw new RuntimeException("Conta não encontrada");
        }

        Conta conta = contaOpt.get();

        // Para conta corrente, considerar limite
        if (conta.getTipoConta() == TipoConta.CORRENTE) {
            Optional<ContaCorrente> ccOpt = contaCorrenteRepository.findByContaIdConta(idConta);
            if (ccOpt.isPresent()) {
                return conta.getSaldo().add(ccOpt.get().getLimite());
            }
        }

        // Para outros tipos, apenas saldo
        return conta.getSaldo();
    }

    /**
     * Verificar se tem saldo suficiente
     */
    @Transactional(readOnly = true)
    public boolean temSaldoSuficiente(Integer idConta, BigDecimal valor) {
        BigDecimal saldoDisponivel = calcularSaldoDisponivel(idConta);
        return saldoDisponivel.compareTo(valor) >= 0;
    }

    /**
     * Validar limites operacionais
     */
    @Transactional(readOnly = true)
    public boolean validarLimiteOperacao(Integer idConta, BigDecimal valor, String tipoOperacao) {
        // Implementar regras específicas de limite
        // Ex: limite diário, limite por operação, etc.
        return true; // Temporário
    }
}