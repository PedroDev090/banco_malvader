package com.malvader.banco.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "historico_encerramento")
public class HistoricoEncerramento {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_hist", columnDefinition = "INT")
    private Integer idHist;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_conta", nullable = false)
    private Conta conta;

    @Column(name = "motivo", nullable = false, length = 200)
    private String motivo;

    @Column(name = "data_hora", nullable = false)
    private LocalDateTime dataHora = LocalDateTime.now();

    // Construtores
    public HistoricoEncerramento() {}

    public HistoricoEncerramento(Conta conta, String motivo) {
        this.conta = conta;
        this.motivo = motivo;
    }

    // Getters e Setters
    public Integer getIdHist() { return idHist; }
    public void setIdHist(Integer idHist) { this.idHist = idHist; }

    public Conta getConta() { return conta; }
    public void setConta(Conta conta) { this.conta = conta; }

    public String getMotivo() { return motivo; }
    public void setMotivo(String motivo) { this.motivo = motivo; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }
}