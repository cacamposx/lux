package br.com.pi.lux.model;

import jakarta.persistence.*;

@Entity
@Table(name = "tb_produto_imagem")
public class ProdutoImagem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Lob
    @Column(nullable = false)
    private byte[] imagem;

    @Column(nullable = false)
    private boolean principal;

    @ManyToOne
    @JoinColumn(name = "produto_id", nullable = false)
    private Produto produto;

    // Construtores
    public ProdutoImagem() {
    }

    public ProdutoImagem(byte[] imagem, boolean principal, Produto produto) {
        this.imagem = imagem;
        this.principal = principal;
        this.produto = produto;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public byte[] getImagem() {
        return imagem;
    }

    public void setImagem(byte[] imagem) {
        this.imagem = imagem;
    }

    public boolean isPrincipal() {
        return principal;
    }

    public void setPrincipal(boolean principal) {
        this.principal = principal;
    }

    public Produto getProduto() {
        return produto;
    }

    public void setProduto(Produto produto) {
        this.produto = produto;
    }
}
