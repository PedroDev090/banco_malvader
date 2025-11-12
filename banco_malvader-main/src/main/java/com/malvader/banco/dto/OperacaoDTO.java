package com.malvader.banco.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class OperacaoDTO {

    @NotNull(message = "Conta origem é obrigatória")
    private Integer idContaOrigem;

    private Integer idContaDestino;

    @NotNull(message = "Tipo de operação é obrigatório")
    private String tipoOperacao; // "DEPOSITO", "SAQUE", "TRANSFERENCIA"

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @Size(max = 100, message = "Descrição muito longa")
    private String descricao;

    // Construtores
    public OperacaoDTO() {}

    public OperacaoDTO(Integer idContaOrigem, Integer idContaDestino, String tipoOperacao,
                       BigDecimal valor, String descricao) {
        this.idContaOrigem = idContaOrigem;
        this.idContaDestino = idContaDestino;
        this.tipoOperacao = tipoOperacao;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Integer getIdContaOrigem() { return idContaOrigem; }
    public void setIdContaOrigem(Integer idContaOrigem) { this.idContaOrigem = idContaOrigem; }

    public Integer getIdContaDestino() { return idContaDestino; }
    public void setIdContaDestino(Integer idContaDestino) { this.idContaDestino = idContaDestino; }

    public String getTipoOperacao() { return tipoOperacao; }
    public void setTipoOperacao(String tipoOperacao) { this.tipoOperacao = tipoOperacao; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}