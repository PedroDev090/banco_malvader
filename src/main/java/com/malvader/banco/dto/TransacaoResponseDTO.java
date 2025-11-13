package com.malvader.banco.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransacaoResponseDTO {

    private Integer idTransacao;
    private String tipoTransacao;
    private BigDecimal valor;
    private LocalDateTime dataHora;
    private String descricao;
    private String contaOrigem;
    private String contaDestino;
    private String status; // "SUCESSO", "FALHA"

    // Construtores
    public TransacaoResponseDTO() {}

    public TransacaoResponseDTO(Integer idTransacao, String tipoTransacao, BigDecimal valor,
                                LocalDateTime dataHora, String descricao, String status) {
        this.idTransacao = idTransacao;
        this.tipoTransacao = tipoTransacao;
        this.valor = valor;
        this.dataHora = dataHora;
        this.descricao = descricao;
        this.status = status;
    }

    // Getters e Setters
    public Integer getIdTransacao() { return idTransacao; }
    public void setIdTransacao(Integer idTransacao) { this.idTransacao = idTransacao; }

    public String getTipoTransacao() { return tipoTransacao; }
    public void setTipoTransacao(String tipoTransacao) { this.tipoTransacao = tipoTransacao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public LocalDateTime getDataHora() { return dataHora; }
    public void setDataHora(LocalDateTime dataHora) { this.dataHora = dataHora; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getContaOrigem() { return contaOrigem; }
    public void setContaOrigem(String contaOrigem) { this.contaOrigem = contaOrigem; }

    public String getContaDestino() { return contaDestino; }
    public void setContaDestino(String contaDestino) { this.contaDestino = contaDestino; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}