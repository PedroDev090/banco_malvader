package com.malvader.banco.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class LoginDTO {

    @NotBlank(message = "CPF é obrigatório")
    @Pattern(regexp = "\\d{11}", message = "CPF deve conter exatamente 11 dígitos")
    private String cpf;

    @NotBlank(message = "Senha é obrigatória")
    @Size(min = 1, message = "Senha não pode estar vazia")
    private String senha;

    @NotBlank(message = "Tipo de acesso é obrigatório")
    private String tipoAcesso; // "FUNCIONARIO" ou "CLIENTE"


    public LoginDTO() {}

    public LoginDTO(String cpf, String senha, String tipoAcesso) {
        this.cpf = cpf;
        this.senha = senha;
        this.tipoAcesso = tipoAcesso;
    }


    public String getCpf() { return cpf; }
    public void setCpf(String cpf) { this.cpf = cpf; }

    public String getSenha() { return senha; }
    public void setSenha(String senha) { this.senha = senha; }

    public String getTipoAcesso() { return tipoAcesso; }
    public void setTipoAcesso(String tipoAcesso) { this.tipoAcesso = tipoAcesso; }

    @Override
    public String toString() {
        return "LoginDTO{" +
                "cpf='" + cpf + '\'' +
                ", tipoAcesso='" + tipoAcesso + '\'' +
                '}';
    }
}
