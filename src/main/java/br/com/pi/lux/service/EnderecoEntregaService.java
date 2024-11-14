package br.com.pi.lux.service;

import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.repository.EnderecoEntregaRepository; // Supondo que você tenha um repositório para EnderecoEntrega
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnderecoEntregaService {

    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository; // Repositório para acessar o banco de dados

    public EnderecoEntrega buscarPorId(int id) {
        return enderecoEntregaRepository.findById(id).orElse(null); // Retorna o EnderecoEntrega ou null caso não encontre
    }
}
