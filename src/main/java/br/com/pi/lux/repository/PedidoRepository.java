package br.com.pi.lux.repository;

import br.com.pi.lux.model.Pedido;
import br.com.pi.lux.model.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Integer> {

    List<Pedido> findByCliente(Cliente cliente); // Busca pedidos pelo cliente
}
