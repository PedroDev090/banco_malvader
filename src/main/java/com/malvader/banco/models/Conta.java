package com.malvader.banco.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "conta")
public class Conta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_conta", columnDefinition = "INT")
    private Integer idConta;

    @Column(name = "numero_conta", nullable = false, unique = true, length = 20)
    private String numeroConta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agencia", nullable = false)
    private Agencia agencia;

    @Column(name = "saldo", nullable = false)
    private BigDecimal saldo = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_conta", nullable = false)
    private TipoConta tipoConta;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private StatusConta status = StatusConta.ATIVA;

    @OneToMany(mappedBy = "contaOrigem", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoesOrigem = new ArrayList<>();

    @OneToMany(mappedBy = "contaDestino", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Transacao> transacoesDestino = new ArrayList<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditoriaAberturaConta> auditorias = new ArrayList<>();

    @OneToMany(mappedBy = "conta", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HistoricoEncerramento> historicosEncerramento = new ArrayList<>();

    // Construtores
    public Conta() {}

    public Conta(String numeroConta, Agencia agencia, BigDecimal saldo, TipoConta tipoConta,
                 Cliente cliente, StatusConta status) {
        this.numeroConta = numeroConta;
        this.agencia = agencia;
        this.saldo = saldo;
        this.tipoConta = tipoConta;
        this.cliente = cliente;
        this.status = status;
    }

    // Getters e Setters
    public Integer getIdConta() { return idConta; }
    public void setIdConta(Integer idConta) { this.idConta = idConta; }

    public String getNumeroConta() { return numeroConta; }
    public void setNumeroConta(String numeroConta) { this.numeroConta = numeroConta; }

    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }

    public BigDecimal getSaldo() { return saldo; }
    public void setSaldo(BigDecimal saldo) { this.saldo = saldo; }

    public TipoConta getTipoConta() { return tipoConta; }
    public void setTipoConta(TipoConta tipoConta) { this.tipoConta = tipoConta; }

    public Cliente getCliente() { return cliente; }
    public void setCliente(Cliente cliente) { this.cliente = cliente; }

    public LocalDateTime getDataAbertura() { return dataAbertura; }
    public void setDataAbertura(LocalDateTime dataAbertura) { this.dataAbertura = dataAbertura; }

    public StatusConta getStatus() { return status; }
    public void setStatus(StatusConta status) { this.status = status; }

    public List<Transacao> getTransacoesOrigem() { return transacoesOrigem; }
    public void setTransacoesOrigem(List<Transacao> transacoesOrigem) { this.transacoesOrigem = transacoesOrigem; }

    public List<Transacao> getTransacoesDestino() { return transacoesDestino; }
    public void setTransacoesDestino(List<Transacao> transacoesDestino) { this.transacoesDestino = transacoesDestino; }

    public List<AuditoriaAberturaConta> getAuditorias() { return auditorias; }
    public void setAuditorias(List<AuditoriaAberturaConta> auditorias) { this.auditorias = auditorias; }

    public List<HistoricoEncerramento> getHistoricosEncerramento() { return historicosEncerramento; }
    public void setHistoricosEncerramento(List<HistoricoEncerramento> historicosEncerramento) { this.historicosEncerramento = historicosEncerramento; }
}

