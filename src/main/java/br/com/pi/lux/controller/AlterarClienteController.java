package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AlterarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/alterarCliente")
    public String mostrarFormularioAlteracao(HttpSession session, Model model) {
        // Recupera o cliente da sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        // Verifica se o cliente está logado
        if (cliente != null) {
            model.addAttribute("cliente", cliente);  // Envia os dados do cliente para o formulário de alteração
            return "alterarCliente";  // Nome da página de alteração (alterarCliente.html)
        }

        // Caso o cliente não esteja logado, redireciona para o login
        return "redirect:/loginCliente";
    }

    @PostMapping("/alterarCliente")
    public String salvarAlteracoes(@ModelAttribute Cliente clienteAlterado, HttpSession session) {
        // Atualiza os dados do cliente
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            cliente.setNome(clienteAlterado.getNome());
            cliente.setEmail(clienteAlterado.getEmail());
            cliente.setCpf(clienteAlterado.getCpf());
            cliente.setDataNascimento(clienteAlterado.getDataNascimento());
            cliente.setGenero(clienteAlterado.getGenero());
            cliente.setStatus(clienteAlterado.getStatus());

            clienteRepository.save(cliente);  // Salva as alterações no banco de dados
            session.setAttribute("cliente", cliente);  // Atualiza a sessão com os novos dados
            return "redirect:/perfilCliente";  // Redireciona de volta para o perfil do cliente
        }

        return "redirect:/loginCliente";  // Caso o cliente não esteja logado
    }
}

