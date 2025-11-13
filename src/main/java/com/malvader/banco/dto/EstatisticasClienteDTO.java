package com.malvader.banco.dto;

import java.math.BigDecimal;

public class EstatisticasClienteDTO {

    private String nomeCliente;
    private Double scoreCredito;
    private Integer totalContas;
    private BigDecimal patrimonioTotal;
    private Integer transacoesMes;
    private BigDecimal movimentacaoMes;

    // Construtores
    public EstatisticasClienteDTO() {}

    // Getters e Setters
    public String getNomeCliente() { return nomeCliente; }
    public void setNomeCliente(String nomeCliente) { this.nomeCliente = nomeCliente; }

    public Double getScoreCredito() { return scoreCredito; }
    public void setScoreCredito(Double scoreCredito) { this.scoreCredito = scoreCredito; }

    public Integer getTotalContas() { return totalContas; }
    public void setTotalContas(Integer totalContas) { this.totalContas = totalContas; }

    public BigDecimal getPatrimonioTotal() { return patrimonioTotal; }
    public void setPatrimonioTotal(BigDecimal patrimonioTotal) { this.patrimonioTotal = patrimonioTotal; }

    public Integer getTransacoesMes() { return transacoesMes; }
    public void setTransacoesMes(Integer transacoesMes) { this.transacoesMes = transacoesMes; }

    public BigDecimal getMovimentacaoMes() { return movimentacaoMes; }
    public void setMovimentacaoMes(BigDecimal movimentacaoMes) { this.movimentacaoMes = movimentacaoMes; }
}