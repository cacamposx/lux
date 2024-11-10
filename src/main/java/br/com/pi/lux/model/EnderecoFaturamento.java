package br.com.pi.lux.model;

import jakarta.persistence.*;
import br.com.pi.lux.model.enums.TipoEndereco;

@Entity
@Table(name = "tb_endereco_faturamento")
public class EnderecoFaturamento{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idEnderecoFaturamento;

    @Column(nullable = false)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(nullable = false)
    private String numero;

    private String complemento;

    @Column(nullable = false)
    private String bairro;

    @Column(nullable = false)
    private String cidade;

    @Column(nullable = false)
    private String uf;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEndereco tipoEndereco;

    @OneToOne(mappedBy = "enderecoFaturamento", cascade = CascadeType.ALL, orphanRemoval = true)
    private Cliente cliente;


    public EnderecoFaturamento(){

    }
    public EnderecoFaturamento(int idEnderecoFaturamento, String cep, String logradouro, String numero, String complemento, String bairro, String cidade, String uf, TipoEndereco tipoEndereco, Cliente cliente) {
        this.idEnderecoFaturamento = idEnderecoFaturamento;
        this.cep = cep;
        this.logradouro = logradouro;
        this.numero = numero;
        this.complemento = complemento;
        this.bairro = bairro;
        this.cidade = cidade;
        this.uf = uf;
        this.tipoEndereco = tipoEndereco;
        this.cliente = cliente;
    }

    public int getIdEnderecoFaturamento() {
        return idEnderecoFaturamento;
    }

    public void setIdEnderecoFaturamento(int idEnderecoFaturamento) {
        this.idEnderecoFaturamento = idEnderecoFaturamento;
    }

    public String getCep() {
        return cep;
    }

    public void setCep(String cep) {
        this.cep = cep;
    }

    public String getLogradouro() {
        return logradouro;
    }

    public void setLogradouro(String logradouro) {
        this.logradouro = logradouro;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getComplemento() {
        return complemento;
    }

    public void setComplemento(String complemento) {
        this.complemento = complemento;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getUf() {
        return uf;
    }

    public void setUf(String uf) {
        this.uf = uf;
    }

    public TipoEndereco getTipoEndereco() {
        return tipoEndereco;
    }

    public void setTipoEndereco(TipoEndereco tipoEndereco) {
        this.tipoEndereco = tipoEndereco;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }
}
