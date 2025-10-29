package com.malvader.banco.dto;

import java.time.LocalDateTime;

public class ExtratoRequestDTO {

    private Integer idConta;
    private LocalDateTime dataInicio;
    private LocalDateTime dataFim;
    private Integer limite = 50; // Limite padrão

    // Construtores
    public ExtratoRequestDTO() {}

    public ExtratoRequestDTO(Integer idConta, LocalDateTime dataInicio, LocalDateTime dataFim) {
        this.idConta = idConta;
        this.dataInicio = dataInicio;
        this.dataFim = dataFim;
    }

    // Getters e Setters
    public Integer getIdConta() { return idConta; }
    public void setIdConta(Integer idConta) { this.idConta = idConta; }

    public LocalDateTime getDataInicio() { return dataInicio; }
    public void setDataInicio(LocalDateTime dataInicio) { this.dataInicio = dataInicio; }

    public LocalDateTime getDataFim() { return dataFim; }
    public void setDataFim(LocalDateTime dataFim) { this.dataFim = dataFim; }

    public Integer getLimite() { return limite; }
    public void setLimite(Integer limite) { this.limite = limite; }
}