package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.repository.ClienteRepository;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;
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

    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            // Se o cliente não estiver logado, redireciona para o cadastro
            return "redirect:/cadastrarCliente";
        }

        // Recupera o carrinho do cliente da sessão
        List<br.com.pi.lux.controller.ItemCarrinho> carrinho = (List<br.com.pi.lux.controller.ItemCarrinho>) session.getAttribute("carrinho");

        if (carrinho == null) {
            // Caso o carrinho não exista, você pode criar uma lista vazia
            carrinho = new ArrayList<>();
        }

        // Adiciona os itens do carrinho no modelo para exibir na página de pagamento
        model.addAttribute("carrinho", carrinho);

        // Passa também o total da compra, incluindo o frete
        double totalCarrinho = calcularTotalCarrinho(carrinho); // Método para calcular o total
        model.addAttribute("totalCarrinho", totalCarrinho);

        // Redireciona para a página de pagamento
        return "pagamento";
    }

    // Método para calcular o total do carrinho
// Corrigir o tipo de carrinho para o correto
    private double calcularTotalCarrinho(List<br.com.pi.lux.controller.ItemCarrinho> carrinho) {
        double total = 0.0;
        for (br.com.pi.lux.controller.ItemCarrinho item : carrinho) {
            total += item.getTotal(); // Calcula o total dos itens (produto * quantidade)
        }
        return total;
    }
}
