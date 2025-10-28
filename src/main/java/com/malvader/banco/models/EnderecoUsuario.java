package com.malvader.banco.models;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco_usuario")
public class EnderecoUsuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco", columnDefinition = "INT")
    private Integer idEndereco;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "cep", nullable = false, length = 10)
    private String cep;

    @Column(name = "local", nullable = false, length = 100)
    private String local;

    @Column(name = "numero_casa", nullable = false)
    private Integer numeroCasa;

    @Column(name = "bairro", nullable = false, length = 50)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 50)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "complemento", length = 50)
    private String complemento;

    // Construtores
    public EnderecoUsuario() {}

    public EnderecoUsuario(Usuario usuario, String cep, String local, Integer numeroCasa,
                           String bairro, String cidade, String estado, String complemento) {
        this.usuario = usuario;
        this.cep = cep;
        this.local = local;
        this.numeroCasa = numeroCasa;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.complemento = complemento;
    }

    // Getters e Setters
    public Integer getIdEndereco() { return idEndereco; }
    public void setIdEndereco(Integer idEndereco) { this.idEndereco = idEndereco; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public Integer getNumeroCasa() { return numeroCasa; }
    public void setNumeroCasa(Integer numeroCasa) { this.numeroCasa = numeroCasa; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }
}