package com.malvader.banco.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.LocalDate;

public class AberturaContaDTO {

    @NotNull(message = "ID do cliente é obrigatório")
    private Integer idCliente;

    @NotNull(message = "ID da agência é obrigatório")
    private Integer idAgencia;

    @NotNull(message = "Tipo de conta é obrigatório")
    private String tipoConta; // "CORRENTE", "POUPANCA", "INVESTIMENTO"

    // Campos específicos para conta corrente
    private BigDecimal limite;
    private LocalDate dataVencimento;
    private BigDecimal taxaManutencao;

    // Campos específicos para conta poupança
    private BigDecimal taxaRendimento;

    // Campos específicos para conta investimento
    private String perfilRisco; // "BAIXO", "MEDIO", "ALTO"
    private BigDecimal valorMinimo;
    private BigDecimal taxaRendimentoBase;

    // Construtores
    public AberturaContaDTO() {}

    // Getters e Setters
    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    public Integer getIdAgencia() { return idAgencia; }
    public void setIdAgencia(Integer idAgencia) { this.idAgencia = idAgencia; }

    public String getTipoConta() { return tipoConta; }
    public void setTipoConta(String tipoConta) { this.tipoConta = tipoConta; }

    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public BigDecimal getTaxaManutencao() { return taxaManutencao; }
    public void setTaxaManutencao(BigDecimal taxaManutencao) { this.taxaManutencao = taxaManutencao; }

    public BigDecimal getTaxaRendimento() { return taxaRendimento; }
    public void setTaxaRendimento(BigDecimal taxaRendimento) { this.taxaRendimento = taxaRendimento; }

    public String getPerfilRisco() { return perfilRisco; }
    public void setPerfilRisco(String perfilRisco) { this.perfilRisco = perfilRisco; }

    public BigDecimal getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(BigDecimal valorMinimo) { this.valorMinimo = valorMinimo; }

    public BigDecimal getTaxaRendimentoBase() { return taxaRendimentoBase; }
    public void setTaxaRendimentoBase(BigDecimal taxaRendimentoBase) { this.taxaRendimentoBase = taxaRendimentoBase; }
}