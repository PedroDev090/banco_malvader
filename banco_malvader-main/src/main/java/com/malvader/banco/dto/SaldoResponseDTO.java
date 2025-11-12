package com.malvader.banco.dto;

import java.math.BigDecimal;

public class SaldoResponseDTO {

    private String numeroConta;
    private BigDecimal saldo;
    private BigDecimal limiteDisponivel;
    private BigDecimal saldoTotal; // saldo + limite
    private String statusConta;

    // Construtores
    public SaldoResponseDTO() {}

    public SaldoResponseDTO(String numeroConta, BigDecimal saldo, BigDecimal limiteDisponivel, String statusConta) {
        this.numeroConta = numeroConta;
        this.saldo = saldo;
        this.limiteDisponivel = limiteDisponivel;
        this.saldoTotal = saldo.add(limiteDisponivel != null ? limiteDisponivel : BigDecimal.ZERO);
        this.statusConta = statusConta;
    }

    // Getters e Setters
    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public BigDecimal getLimiteDisponivel() { return limiteDisponivel; }
    public void setLimiteDisponivel(BigDecimal limiteDisponivel) { this.limiteDisponivel = limiteDisponivel; }

    public BigDecimal getSaldoTotal() { return saldoTotal; }
    public void setSaldoTotal(BigDecimal saldoTotal) { this.saldoTotal = saldoTotal; }

    public String getStatusConta() { return statusConta; }
    public void setStatusConta(String statusConta) { this.statusConta = statusConta; }
}