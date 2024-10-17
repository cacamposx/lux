package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginClienteController {
    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/loginCliente")
    public String mostrarLoginCliente(Model model) {
        return "login";
    }

    @PostMapping("/loginCliente")
    public String autenticarCliente(
            @RequestParam String email,
            @RequestParam String senha,
            Model model,
            HttpSession session) {

        Optional<Cliente> clienteOptional = clienteRepository.findByEmail(email);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();
            session.setAttribute("cliente", cliente);

            if (BCrypt.checkpw(senha, cliente.getSenha())) {
                model.addAttribute("mensagem", "Login realizado com sucesso!");

                return "redirect:/homeEcommerce";

            } else {
                model.addAttribute("mensagem", "Senha incorreta!");
                return "loginCliente";
            }
        } else {
            model.addAttribute("mensagem", "E-mail não cadastrado!");
            return "loginCliente";
        }
    }
}
