package com.malvader.banco.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agencia")
public class Agencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_agencia", columnDefinition = "INT")
    private Integer idAgencia;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "codigo_agencia", nullable = false, unique = true, length = 10)
    private String codigoAgencia;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "endereco_id", nullable = false)
    private EnderecoAgencia endereco;

    @OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Funcionario> funcionarios = new ArrayList<>();

    @OneToMany(mappedBy = "agencia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Conta> contas = new ArrayList<>();

    public Agencia() {}

    public Agencia(String nome, String codigoAgencia, EnderecoAgencia endereco) {
        this.nome = nome;
        this.codigoAgencia = codigoAgencia;
        this.endereco = endereco;
    }


    public Integer getIdAgencia() { return idAgencia; }
    public void setIdAgencia(Integer idAgencia) { this.idAgencia = idAgencia; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCodigoAgencia() { return codigoAgencia; }
    public void setCodigoAgencia(String codigoAgencia) { this.codigoAgencia = codigoAgencia; }

    public EnderecoAgencia getEndereco() { return endereco; }
    public void setEndereco(EnderecoAgencia endereco) { this.endereco = endereco; }

    public List<Funcionario> getFuncionarios() { return funcionarios; }
    public void setFuncionarios(List<Funcionario> funcionarios) { this.funcionarios = funcionarios; }

    public List<Conta> getContas() { return contas; }
    public void setContas(List<Conta> contas) { this.contas = contas; }
}