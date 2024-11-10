package br.com.pi.lux.service;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.EnderecoFaturamento;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.repository.EnderecoFaturamentoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private EnderecoFaturamentoRepository enderecoFaturamentoRepository; // Usando o repositório correto

    public Cliente cadastrarCliente(Cliente cliente) {
        // Primeiro, salva o endereço de faturamento
        EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();
        enderecoFaturamentoRepository.save(enderecoFaturamento); // Salvar o endereço de faturamento no banco de dados

        // Agora, associa o endereço de faturamento ao cliente
        cliente.setEnderecoFaturamento(enderecoFaturamento);  // Associando o EnderecoFaturamento ao Cliente

        // Depois, salva o cliente
        return clienteRepository.save(cliente);  // Salvar o cliente no banco de dados
    }

    public void excluirTodosClientes() {
        clienteRepository.deleteAll();
    }
}
