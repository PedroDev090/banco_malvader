package com.malvader.banco.models;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "conta_investimento")
public class ContaInvestimento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta_investimento", columnDefinition = "INT")
    private Integer idContaInvestimento;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false, unique = true)
    private Conta conta;

    @Enumerated(EnumType.STRING)
    @Column(name = "perfil_risco", nullable = false)
    private PerfilRisco perfilRisco;

    @Column(name = "valor_minimo", nullable = false)
    private BigDecimal valorMinimo;

    @Column(name = "taxa_rendimento_base", nullable = false)
    private BigDecimal taxaRendimentoBase;

    // Construtores
    public ContaInvestimento() {}

    public ContaInvestimento(Conta conta, PerfilRisco perfilRisco, BigDecimal valorMinimo, BigDecimal taxaRendimentoBase) {
        this.conta = conta;
        this.perfilRisco = perfilRisco;
        this.valorMinimo = valorMinimo;
        this.taxaRendimentoBase = taxaRendimentoBase;
    }

    // Getters e Setters
    public Integer getIdContaInvestimento() { return idContaInvestimento; }
    public void setIdContaInvestimento(Integer idContaInvestimento) { this.idContaInvestimento = idContaInvestimento; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public PerfilRisco getPerfilRisco() { return perfilRisco; }
    public void setPerfilRisco(PerfilRisco perfilRisco) { this.perfilRisco = perfilRisco; }

    public BigDecimal getValorMinimo() { return valorMinimo; }
    public void setValorMinimo(BigDecimal valorMinimo) { this.valorMinimo = valorMinimo; }

    public BigDecimal getTaxaRendimentoBase() { return taxaRendimentoBase; }
    public void setTaxaRendimentoBase(BigDecimal taxaRendimentoBase) { this.taxaRendimentoBase = taxaRendimentoBase; }
}

