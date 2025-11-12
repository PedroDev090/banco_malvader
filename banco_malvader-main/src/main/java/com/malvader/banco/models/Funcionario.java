package com.malvader.banco.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "funcionario")
public class Funcionario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_funcionario", columnDefinition = "INT")
    private Integer idFuncionario;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_agencia", nullable = false)
    private Agencia agencia;

    @Column(name = "codigo_funcionario", nullable = false, unique = true, length = 20)
    private String codigoFuncionario;

    @Enumerated(EnumType.STRING)
    @Column(name = "cargo", nullable = false)
    private Cargo cargo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_supervisor")
    private Funcionario supervisor;

    @OneToMany(mappedBy = "supervisor", fetch = FetchType.LAZY)
    private List<Funcionario> supervisionados = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Relatorio> relatorios = new ArrayList<>();

    @OneToMany(mappedBy = "funcionario", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<AuditoriaAberturaConta> auditorias = new ArrayList<>();

    // Construtores
    public Funcionario() {}

    public Funcionario(Usuario usuario, Agencia agencia, String codigoFuncionario, Cargo cargo) {
        this.usuario = usuario;
        this.agencia = agencia;
        this.codigoFuncionario = codigoFuncionario;
        this.cargo = cargo;
    }

    // Getters e Setters
    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }

    public String getCodigoFuncionario() { return codigoFuncionario; }
    public void setCodigoFuncionario(String codigoFuncionario) { this.codigoFuncionario = codigoFuncionario; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public Funcionario getSupervisor() { return supervisor; }
    public void setSupervisor(Funcionario supervisor) { this.supervisor = supervisor; }

    public List<Funcionario> getSupervisionados() { return supervisionados; }
    public void setSupervisionados(List<Funcionario> supervisionados) { this.supervisionados = supervisionados; }

    public List<Relatorio> getRelatorios() { return relatorios; }
    public void setRelatorios(List<Relatorio> relatorios) { this.relatorios = relatorios; }

    public List<AuditoriaAberturaConta> getAuditorias() { return auditorias; }
    public void setAuditorias(List<AuditoriaAberturaConta> auditorias) { this.auditorias = auditorias; }
}

