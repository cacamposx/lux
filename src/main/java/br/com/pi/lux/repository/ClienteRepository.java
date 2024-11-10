package br.com.pi.lux.repository;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Integer> {

    Optional<Cliente> findByEmail(String email);
    Optional<Cliente> findByCpf(String cpf);
    List<Cliente> findByNomeContainingIgnoreCase(String filtroNome);   //Método para filtrar por nome

    // Método para excluir todos os clientes
    void deleteAll();
}
