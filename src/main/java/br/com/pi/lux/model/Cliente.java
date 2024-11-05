package br.com.pi.lux.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_cliente")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idCliente;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true)
    private String cpf;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private LocalDate dataNascimento;

    @Column(nullable = false)
    private String genero;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_faturamento_id", nullable = false)
    private Endereco enderecoFaturamento;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<Endereco> enderecosEntrega = new ArrayList<>();

    @Column(nullable = false)
    private boolean status;

    // Construtores, getters e setters

    public Cliente() {}

    public Cliente(String nome, String cpf, String email, String senha, LocalDate dataNascimento, String genero, Endereco enderecoFaturamento, boolean status) {
        this.nome = nome;
        this.cpf = cpf;
        this.email = email;
        this.senha = senha;
        this.dataNascimento = dataNascimento;
        this.genero = genero;
        this.enderecoFaturamento = enderecoFaturamento;
        this.status = status;
    }

    // Métodos auxiliares para gerenciamento de endereços de entrega
    public void addEnderecoEntrega(Endereco endereco) {
        enderecosEntrega.add(endereco);
        endereco.setCliente(this);
    }

    public void removeEnderecoEntrega(Endereco endereco) {
        enderecosEntrega.remove(endereco);
        endereco.setCliente(null);
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public void setDataNascimento(LocalDate dataNascimento) {
        this.dataNascimento = dataNascimento;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public Endereco getEnderecoFaturamento() {
        return enderecoFaturamento;
    }

    public void setEnderecoFaturamento(Endereco enderecoFaturamento) {
        this.enderecoFaturamento = enderecoFaturamento;
    }

    public List<Endereco> getEnderecosEntrega() {
        return enderecosEntrega;
    }

    public void setEnderecosEntrega(List<Endereco> enderecosEntrega) {
        this.enderecosEntrega = enderecosEntrega;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
