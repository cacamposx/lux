package br.com.pi.lux.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_produto")
public class Produto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idProduto;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false)
    private String descricao;

    @Column(nullable = false)
    private int quantidade;

    @Column(nullable = false)
    private double preco;

    @Column(nullable = false)
    private double avaliacao;

    @Column(nullable = false)
    private boolean status;

    @OneToMany(mappedBy = "produto", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProdutoImagem> imagens;

    @Column(nullable = false)
    private LocalDateTime data;

    @Transient
    private String base64Image;

    @PrePersist
    protected void onCreate() {
        this.data = LocalDateTime.now();
    }

    // Construtores
    public Produto() {
    }

    public Produto(int idProduto, String nome, String descricao, int quantidade, double preco, double avaliacao, boolean status, List<ProdutoImagem> imagens, LocalDateTime data) {
        this.idProduto = idProduto;
        this.nome = nome;
        this.descricao = descricao;
        this.quantidade = quantidade;
        this.preco = preco;
        this.avaliacao = avaliacao;
        this.status = status;
        this.imagens = imagens;
        this.data = data;
    }

    // Getters e Setters

    public int getIdProduto() {
        return idProduto;
    }

    public void setIdProduto(int idProduto) {
        this.idProduto = idProduto;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(int quantidade) {
        this.quantidade = quantidade;
    }

    public double getPreco() {
        return preco;
    }

    public void setPreco(double preco) {
        this.preco = preco;
    }

    public double getAvaliacao() {
        return avaliacao;
    }

    public void setAvaliacao(double avaliacao) {
        this.avaliacao = avaliacao;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public LocalDateTime getData() {
        return data;
    }

    public void setData(LocalDateTime data) {
        this.data = data;
    }

    public List<ProdutoImagem> getImagens() {
        return imagens;
    }

    public void setImagens(List<ProdutoImagem> imagens) {
        this.imagens = imagens;
    }

    public String getBase64Image() {
        return base64Image;
    }

    public void setBase64Image(String base64Image) {
        this.base64Image = base64Image;
    }

    // Método para obter a imagem principal
    public ProdutoImagem getImagemPrincipal() {
        return imagens.stream().filter(ProdutoImagem::isPrincipal).findFirst().orElse(null);
    }

}
