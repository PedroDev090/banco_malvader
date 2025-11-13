package com.malvader.banco.dto;

import com.malvader.banco.models.Cargo;

public class LoginResponseDTO {
    private boolean sucesso;
    private String mensagem;
    private String tipoUsuario;
    private String nome;
    private Integer idUsuario;
    private Cargo cargo; // apenas para funcionários
    private String redirectUrl;


    public LoginResponseDTO() {}

    public LoginResponseDTO(boolean sucesso, String mensagem, String tipoUsuario,
                            String nome, Integer idUsuario, Cargo cargo, String redirectUrl) {
        this.sucesso = sucesso;
        this.mensagem = mensagem;
        this.tipoUsuario = tipoUsuario;
        this.nome = nome;
        this.idUsuario = idUsuario;
        this.cargo = cargo;
        this.redirectUrl = redirectUrl;
    }


    public static LoginResponseDTO sucesso(String tipoUsuario, String nome,
                                           Integer idUsuario, Cargo cargo, String redirectUrl) {
        return new LoginResponseDTO(true, "Login realizado com sucesso",
                tipoUsuario, nome, idUsuario, cargo, redirectUrl);
    }


    public static LoginResponseDTO erro(String mensagem) {
        return new LoginResponseDTO(false, mensagem, null, null, null, null, null);
    }


    public boolean isSucesso() { return sucesso; }
    public void setSucesso(boolean sucesso) { this.sucesso = sucesso; }

    public String getMensagem() { return mensagem; }
    public void setMensagem(String mensagem) { this.mensagem = mensagem; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getNome() { return nome; }
    public void setNome(String nome) { this.nome = nome; }

    public Integer getIdUsuario() { return idUsuario; }
    public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

    public Cargo getCargo() { return cargo; }
    public void setCargo(Cargo cargo) { this.cargo = cargo; }

    public String getRedirectUrl() { return redirectUrl; }
    public void setRedirectUrl(String redirectUrl) { this.redirectUrl = redirectUrl; }

    @Override
    public String toString() {
        return "LoginResponseDTO{" +
                "sucesso=" + sucesso +
                ", mensagem='" + mensagem + '\'' +
                ", tipoUsuario='" + tipoUsuario + '\'' +
                ", nome='" + nome + '\'' +
                ", idUsuario=" + idUsuario +
                ", cargo=" + cargo +
                ", redirectUrl='" + redirectUrl + '\'' +
                '}';
    }
}