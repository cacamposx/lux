package br.com.pi.lux.model;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tb_pedido")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idPedido;

    @ManyToOne
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    private double frete;

    @ManyToOne
    @JoinColumn(name = "endereco_entrega_id", nullable = false)
    private EnderecoEntrega enderecoEntrega;

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItemPedido> itens = new ArrayList<>();

    @Column(nullable = false)
    private String status;

    @Column(nullable = false)
    private String formaPagamento;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private double valorTotal;

    // Construtores, getters e setters

    public Pedido() {
    }

    public Pedido(Cliente cliente, EnderecoEntrega enderecoEntrega, List<ItemPedido> itens, String status, String formaPagamento, LocalDate data, double valorTotal) {
        this.cliente = cliente;
        this.enderecoEntrega = enderecoEntrega;
        this.itens = (itens == null) ? new ArrayList<>() : itens; // Inicializa a lista se for nula
        this.status = (status == null || status.isEmpty()) ? "Aguardando Pagamento" : status; // Definindo status padrão
        this.formaPagamento = formaPagamento;
        this.data = LocalDate.now();
        this.valorTotal = valorTotal;
    }

    public int getIdPedido() {
        return idPedido;
    }

    public void setIdPedido(int idPedido) {
        this.idPedido = idPedido;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public EnderecoEntrega getEnderecoEntrega() {
        return enderecoEntrega;
    }

    public void setEnderecoEntrega(EnderecoEntrega enderecoEntrega) {
        this.enderecoEntrega = enderecoEntrega;
    }

    public List<ItemPedido> getItens() {
        return itens;
    }

    public void setItens(List<ItemPedido> itens) {
        this.itens = itens;
        calcularValorTotal(); // Recalcula o valor total ao definir os itens
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFrete() {
        return frete;
    }

    public void setFrete(double frete) {
        this.frete = frete;
        calcularValorTotal(); // Recalcula o valor total ao definir o frete
    }

    public String getFormaPagamento() {
        return formaPagamento;
    }

    public void setFormaPagamento(String formaPagamento) {
        this.formaPagamento = formaPagamento;
    }

    public LocalDate getData() {
        return data;
    }

    public void setData(LocalDate data) {
        this.data = data;
    }

    public double getValorTotal() {
        return valorTotal;
    }

    public void setValorTotal(double valorTotal) {
        this.valorTotal = valorTotal;
    }

    // Método para calcular o valor total
    public void calcularValorTotal() {
        this.valorTotal = itens.stream().mapToDouble(ItemPedido::getTotal).sum() + this.frete;
    }

    // Método para validar o pedido
    public boolean validarPedido() {
        return cliente != null && enderecoEntrega != null && !itens.isEmpty() && frete >= 0;
    }

    // Métodos para adicionar e remover itens
    public void adicionarItem(ItemPedido item) {
        this.itens.add(item);
        calcularValorTotal();  // Recalcula o valor total
    }

    public void removerItem(ItemPedido item) {
        this.itens.remove(item);
        calcularValorTotal();  // Recalcula o valor total
    }
}
