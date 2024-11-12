package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AlterarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/alterarCliente")
    @Transactional  // Garante que a sessão esteja aberta durante o processo
    public String mostrarFormularioAlteracao(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            // Verifica o nome do cliente no log (para depuração)
            System.out.println(cliente.getNome());

            // Inicializa a coleção de endereçosEntrega para evitar LazyInitializationException
            if (cliente.getEnderecosEntrega() != null) {
                // A sessão do Hibernate estará aberta por conta da anotação @Transactional
                cliente.getEnderecosEntrega().size();  // Isso força a inicialização da coleção
            }

            // Passa o cliente para a view
            model.addAttribute("cliente", cliente);
            return "alterarCliente";
        }

        // Se o cliente não estiver logado, redireciona para a página de login
        return "redirect:/loginCliente";
    }

    @PostMapping("/alterarCliente")
    @Transactional  // Garante que a sessão esteja aberta durante o processo
    public String salvarAlteracoes(@ModelAttribute Cliente clienteAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            // Atualiza os dados do cliente
            cliente.setNome(clienteAlterado.getNome());
            cliente.setEmail(clienteAlterado.getEmail());
            cliente.setCpf(clienteAlterado.getCpf());
            cliente.setDataNascimento(clienteAlterado.getDataNascimento());
            cliente.setGenero(clienteAlterado.getGenero());
            cliente.setStatus(clienteAlterado.getStatus());

            // Atualiza o endereço de faturamento
            if (clienteAlterado.getEnderecoFaturamento() != null) {
                cliente.setEnderecoFaturamento(clienteAlterado.getEnderecoFaturamento());
            }

            // Atualiza os endereços de entrega
            if (clienteAlterado.getEnderecosEntrega() != null) {
                cliente.setEnderecosEntrega(clienteAlterado.getEnderecosEntrega());
            }

            // Salva as alterações no banco
            clienteRepository.save(cliente);

            // Atualiza a sessão com os novos dados do cliente
            session.setAttribute("cliente", cliente);

            // Redireciona para o perfil do cliente após a atualização
            return "redirect:/perfilCliente";
        }

        // Se o cliente não estiver logado, redireciona para a página de login
        return "redirect:/loginCliente";
    }
}
