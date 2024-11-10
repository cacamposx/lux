package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PerfilClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/perfilCliente")
    public String mostrarPerfilCliente(Model model, HttpSession session) {
        // Recupera o cliente da sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        // Verifica se o cliente está na sessão
        if (cliente != null) {
            model.addAttribute("cliente", cliente);
            return "perfilCliente";  // Nome da página .html Thymeleaf
        }

        // Caso o cliente não esteja logado, redireciona para o login
        model.addAttribute("mensagem", "Sessão expirada. Faça login novamente.");
        return "redirect:/loginCliente";
    }

}
