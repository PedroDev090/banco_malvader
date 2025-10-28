package com.malvader.banco.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "conta_corrente")
public class ContaCorrente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta_corrente", columnDefinition = "INT")
    private Integer idContaCorrente;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false, unique = true)
    private Conta conta;

    @Column(name = "limite", nullable = false)
    private BigDecimal limite = BigDecimal.ZERO;

    @Column(name = "data_vencimento", nullable = false)
    private LocalDate dataVencimento;

    @Column(name = "taxa_manutencao", nullable = false)
    private BigDecimal taxaManutencao = BigDecimal.ZERO;

    // Construtores
    public ContaCorrente() {}

    public ContaCorrente(Conta conta, BigDecimal limite, LocalDate dataVencimento, BigDecimal taxaManutencao) {
        this.conta = conta;
        this.limite = limite;
        this.dataVencimento = dataVencimento;
        this.taxaManutencao = taxaManutencao;
    }

    // Getters e Setters
    public Integer getIdContaCorrente() { return idContaCorrente; }
    public void setIdContaCorrente(Integer idContaCorrente) { this.idContaCorrente = idContaCorrente; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public BigDecimal getLimite() { return limite; }
    public void setLimite(BigDecimal limite) { this.limite = limite; }

    public LocalDate getDataVencimento() { return dataVencimento; }
    public void setDataVencimento(LocalDate dataVencimento) { this.dataVencimento = dataVencimento; }

    public BigDecimal getTaxaManutencao() { return taxaManutencao; }
    public void setTaxaManutencao(BigDecimal taxaManutencao) { this.taxaManutencao = taxaManutencao; }
}