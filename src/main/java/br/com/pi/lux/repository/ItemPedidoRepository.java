package br.com.pi.lux.repository;

import br.com.pi.lux.model.ItemPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
    public interface ItemPedidoRepository extends JpaRepository<ItemPedido, Integer> {
        List<ItemPedido> findByPedido_IdPedido(int pedidoId); // Busca os itens de um pedido pelo ID do pedido
    }

