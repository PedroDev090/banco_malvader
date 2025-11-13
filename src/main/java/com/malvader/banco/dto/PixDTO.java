package com.malvader.banco.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public class PixDTO {

    @NotNull(message = "Conta origem é obrigatória")
    private Integer idContaOrigem;

    @NotNull(message = "Chave PIX é obrigatória")
    @Size(min = 5, max = 100, message = "Chave PIX deve ter entre 5 e 100 caracteres")
    private String chavePix;

    @NotNull(message = "Valor é obrigatório")
    @DecimalMin(value = "0.01", message = "Valor deve ser maior que zero")
    private BigDecimal valor;

    @Size(max = 100, message = "Descrição muito longa")
    private String descricao;

    // Construtores
    public PixDTO() {}

    public PixDTO(Integer idContaOrigem, String chavePix, BigDecimal valor, String descricao) {
        this.idContaOrigem = idContaOrigem;
        this.chavePix = chavePix;
        this.valor = valor;
        this.descricao = descricao;
    }

    // Getters e Setters
    public Integer getIdContaOrigem() { return idContaOrigem; }
    public void setIdContaOrigem(Integer idContaOrigem) { this.idContaOrigem = idContaOrigem; }

    public String getChavePix() { return chavePix; }
    public void setChavePix(String chavePix) { this.chavePix = chavePix; }

    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }
}