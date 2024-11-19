package br.com.pi.lux.service;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.Pedido;
import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.ItemPedido;
import br.com.pi.lux.repository.PedidoRepository;
import br.com.pi.lux.repository.EnderecoEntregaRepository;
import br.com.pi.lux.repository.ItemPedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository;

    @Autowired
    private ItemPedidoRepository itemPedidoRepository;

    public Pedido salvar(Pedido pedido) {
        pedido.calcularValorTotal();

        return pedidoRepository.save(pedido);
    }

    public Pedido finalizarPedido(Pedido pedido, double freteEscolhido, String formaPagamento, int enderecoEntregaId) {
        // Define a forma de pagamento e o valor do frete
        pedido.setFormaPagamento(formaPagamento);
        pedido.setValorTotal(pedido.getValorTotal() + freteEscolhido); // Adiciona o valor do frete ao total

        // Encontrar o endereço de entrega pelo ID
        Optional<EnderecoEntrega> enderecoEntregaOpt = enderecoEntregaRepository.findById(enderecoEntregaId);
        if (enderecoEntregaOpt.isPresent()) {
            pedido.setEnderecoEntrega(enderecoEntregaOpt.get()); // Associa o endereço de entrega ao pedido
        } else {
            throw new IllegalArgumentException("Endereço de entrega não encontrado!");
        }

        // Atualiza o pedido na base de dados
        return pedidoRepository.save(pedido);
    }

    public void adicionarItem(Pedido pedido, ItemPedido item) {
        pedido.adicionarItem(item); // Adiciona o item ao pedido e recalcula o valor total
        itemPedidoRepository.save(item); // Salva o item do pedido no banco de dados
    }


    public void removerItem(Pedido pedido, ItemPedido item) {
        pedido.removerItem(item); // Remove o item do pedido e recalcula o valor total
        itemPedidoRepository.delete(item); // Exclui o item do banco de dados
    }

    public void atualizarQuantidade(Pedido pedido, ItemPedido item, int quantidade) {
        item.setQuantidade(quantidade); // Atualiza a quantidade
        item.setTotal(item.getPrecoUnitario() * quantidade); // Recalcula o total do item
        pedido.calcularValorTotal(); // Recalcula o valor total do pedido
        itemPedidoRepository.save(item); // Atualiza o item no banco de dados
        pedidoRepository.save(pedido); // Atualiza o pedido no banco de dados
    }


    public void calcularValorTotal(Pedido pedido) {
        pedido.calcularValorTotal(); // Chama o método do modelo Pedido para recalcular o valor total
    }

    public List<Pedido> buscarPedidosPorCliente(Cliente cliente) {
        return pedidoRepository.findByCliente(cliente);
    }

    public List<Pedido> listarPedidos() {
        return pedidoRepository.findAll(); // Retorna todos os pedidos
    }

    public Pedido buscarPorId(int id) {
        Optional<Pedido> pedidoOptional = pedidoRepository.findById(id);
        return pedidoOptional.orElse(null);
    }

    public Pedido salvarPedido(Pedido pedido) {
        return pedidoRepository.save(pedido);
    }

}
