package com.malvader.banco.models;

import jakarta.persistence.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import java.time.LocalDateTime;

@Entity
@Table(name = "relatorio")
public class Relatorio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_relatorio", columnDefinition = "INT")
    private Integer idRelatorio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_funcionario", nullable = false)
    private Funcionario funcionario;

    @Column(name = "tipo_relatorio", nullable = false, length = 50)
    private String tipoRelatorio;

    @Column(name = "data_geracao", nullable = false)
    private LocalDateTime dataGeracao = LocalDateTime.now();

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "conteudo", nullable = false, columnDefinition = "json")
    private String conteudo;

    // Construtores
    public Relatorio() {}

    public Relatorio(Funcionario funcionario, String tipoRelatorio, String conteudo) {
        this.funcionario = funcionario;
        this.tipoRelatorio = tipoRelatorio;
        this.conteudo = conteudo;
    }

    // Getters e Setters
    public Integer getIdRelatorio() { return idRelatorio; }
    public void setIdRelatorio(Integer idRelatorio) { this.idRelatorio = idRelatorio; }

    public Funcionario getFuncionario() { return funcionario; }
    public void setFuncionario(Funcionario funcionario) { this.funcionario = funcionario; }

    public String getTipoRelatorio() { return tipoRelatorio; }
    public void setTipoRelatorio(String tipoRelatorio) { this.tipoRelatorio = tipoRelatorio; }

    public LocalDateTime getDataGeracao() { return dataGeracao; }
    public void setDataGeracao(LocalDateTime dataGeracao) { this.dataGeracao = dataGeracao; }

    public String getConteudo() { return conteudo; }
    public void setConteudo(String conteudo) { this.conteudo = conteudo; }
}