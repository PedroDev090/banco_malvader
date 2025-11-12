package com.malvader.banco.dto;

// DTO para sessão do usuário (opcional - pode ser usado para armazenar na sessão)
public class UsuarioSessaoDTO {
    private Integer idUsuario;
    private String nome;
    private String cpf;
    private String tipoUsuario;
    private String cargo; // apenas para funcionários
    private Integer idFuncionario; // apenas para funcionários
    private Integer idCliente; // apenas para clientes

    // Construtores
    public UsuarioSessaoDTO() {}

    public UsuarioSessaoDTO(Integer idUsuario, String nome, String cpf, String tipoUsuario) {
        this.idUsuario = idUsuario;
        this.nome = nome;
        this.cpf = cpf;
        this.tipoUsuario = tipoUsuario;
    }

    // Getters e Setters
    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getCargo() { return cargo; }
    public void setCargo(String cargo) { this.cargo = cargo; }

    public Integer getIdFuncionario() { return idFuncionario; }
    public void setIdFuncionario(Integer idFuncionario) { this.idFuncionario = idFuncionario; }

    public Integer getIdCliente() { return idCliente; }
    public void setIdCliente(Integer idCliente) { this.idCliente = idCliente; }

    @Override
    public String toString() {
        return "UsuarioSessaoDTO{" +
                "idUsuario=" + idUsuario +
                ", nome='" + nome + '\'' +
                ", cpf='" + cpf + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", cargo='" + cargo + '\'' +
                ", idFuncionario=" + idFuncionario +
                ", idCliente=" + idCliente +
                '}';
    }
}