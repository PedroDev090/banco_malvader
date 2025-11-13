package com.malvader.banco.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "auditoria_abertura_conta")
public class AuditoriaAberturaConta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_auditoria", columnDefinition = "INT")
    private Integer idAuditoria;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario")
    private Funcionario funcionario;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    @Column(name = "observacao", length = 200)
    private String observacao;

    // Construtores
    public AuditoriaAberturaConta() {}

    public AuditoriaAberturaConta(Conta conta, Funcionario funcionario, String observacao) {
        this.conta = conta;
        this.funcionario = funcionario;
        this.observacao = observacao;
    }

    // Getters e Setters
    public Integer getIdAuditoria() { return idAuditoria; }
    public void setIdAuditoria(Integer idAuditoria) { this.idAuditoria = idAuditoria; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getObservacao() { return observacao; }
    public void setObservacao(String observacao) { this.observacao = observacao; }
}