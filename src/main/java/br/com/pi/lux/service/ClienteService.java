package br.com.pi.lux.service;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.EnderecoFaturamento;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.repository.EnderecoEntregaRepository;
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
    private EnderecoFaturamentoRepository enderecoFaturamentoRepository;

    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository;

    public Cliente cadastrarCliente(Cliente cliente) {
        // Verificar e salvar o endereço de faturamento
        if (cliente.getEnderecoFaturamento() != null) {
            EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();
            enderecoFaturamento.setCliente(cliente);  // Associando o cliente ao endereço de faturamento
            enderecoFaturamentoRepository.save(enderecoFaturamento);  // Salvar no banco
        }

        // Verificar e salvar os endereços de entrega
        if (cliente.getEnderecosEntrega() != null) {
            for (EnderecoEntrega enderecoEntrega : cliente.getEnderecosEntrega()) {
                enderecoEntrega.setCliente(cliente);  // Associando o cliente a cada endereço de entrega
                enderecoEntregaRepository.save(enderecoEntrega);  // Salvar no banco
            }
        }

        // Agora, salva o cliente
        return clienteRepository.save(cliente);
    }
}

