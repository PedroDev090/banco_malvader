package com.malvader.banco.models;

import jakarta.persistence.*;

@Entity
@Table(name = "endereco_agencia")
public class EnderecoAgencia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_endereco_agencia", columnDefinition = "INT")
    private Integer idEnderecoAgencia;

    @Column(name = "cep", nullable = false, length = 10)
    private String cep;

    @Column(name = "local", nullable = false, length = 100)
    private String local;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "bairro", nullable = false, length = 50)
    private String bairro;

    @Column(name = "cidade", nullable = false, length = 50)
    private String cidade;

    @Column(name = "estado", nullable = false, length = 2)
    private String estado;

    @Column(name = "complemento", length = 50)
    private String complemento;

    @OneToOne(mappedBy = "endereco", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Agencia agencia;

    // Construtores
    public EnderecoAgencia() {}

    public EnderecoAgencia(String cep, String local, Integer numero, String bairro,
                           String cidade, String estado, String complemento) {
        this.cep = cep;
        this.local = local;
        this.numero = numero;
        this.bairro = bairro;
        this.cidade = cidade;
        this.estado = estado;
        this.complemento = complemento;
    }

    // Getters e Setters
    public Integer getIdEnderecoAgencia() { return idEnderecoAgencia; }
    public void setIdEnderecoAgencia(Integer idEnderecoAgencia) { this.idEnderecoAgencia = idEnderecoAgencia; }

    public String getCep() { return cep; }
    public void setCep(String cep) { this.cep = cep; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public Integer getNumero() { return numero; }
    public void setNumero(Integer numero) { this.numero = numero; }

    public String getBairro() { return bairro; }
    public void setBairro(String bairro) { this.bairro = bairro; }

    public String getCidade() { return cidade; }
    public void setCidade(String cidade) { this.cidade = cidade; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getComplemento() { return complemento; }
    public void setComplemento(String complemento) { this.complemento = complemento; }

    public Agencia getAgencia() { return agencia; }
    public void setAgencia(Agencia agencia) { this.agencia = agencia; }
}