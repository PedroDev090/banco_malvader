package com.malvader.banco.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta_poupanca")
public class ContaPoupanca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta_poupanca", columnDefinition = "INT")
    private Integer idContaPoupanca;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false, unique = true)
    private Conta conta;

    @Column(name = "taxa_rendimento", nullable = false)
    private BigDecimal taxaRendimento;

    @Column(name = "ultimo_rendimento")
    private LocalDateTime ultimoRendimento;

    // Construtores
    public ContaPoupanca() {}

    public ContaPoupanca(Conta conta, BigDecimal taxaRendimento) {
        this.conta = conta;
        this.taxaRendimento = taxaRendimento;
    }

    // Getters e Setters
    public Integer getIdContaPoupanca() { return idContaPoupanca; }
    public void setIdContaPoupanca(Integer idContaPoupanca) { this.idContaPoupanca = idContaPoupanca; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public BigDecimal getTaxaRendimento() { return taxaRendimento; }
    public void setTaxaRendimento(BigDecimal taxaRendimento) { this.taxaRendimento = taxaRendimento; }

    public LocalDateTime getUltimoRendimento() { return ultimoRendimento; }
    public void setUltimoRendimento(LocalDateTime ultimoRendimento) { this.ultimoRendimento = ultimoRendimento; }
}