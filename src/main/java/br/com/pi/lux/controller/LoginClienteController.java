package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @GetMapping("/loginCliente")
    public String mostrarLoginCliente(HttpSession session) {
        // Limpa qualquer mensagem de erro anterior
        session.removeAttribute("mensagem");
        return "loginCliente";
    }

    @PostMapping("/loginCliente")
    public String autenticarCliente(@RequestParam String email,
                                    @RequestParam String senha,
                                    HttpSession session) {

        Optional<Cliente> clienteOptional = clienteRepository.findByEmail(email);

        if (clienteOptional.isPresent()) {
            Cliente cliente = clienteOptional.get();

            if (BCrypt.checkpw(senha, cliente.getSenha())) {
                session.setAttribute("cliente", cliente); // Armazena o cliente na sessão
                return "redirect:/perfilCliente";  // Redireciona para o perfil do cliente
            } else {
                session.setAttribute("mensagem", "Senha incorreta!"); // Mensagem de erro para senha incorreta
                return "redirect:/loginCliente"; // Redireciona de volta para a página de login
            }
        } else {
            session.setAttribute("mensagem", "E-mail não cadastrado!"); // Mensagem para email não encontrado
            return "redirect:/loginCliente"; // Redireciona de volta para a página de login
        }
    }
}
