package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.Endereco;
import br.com.pi.lux.repository.ClienteRepository;
import jakarta.servlet.http.HttpSession;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class AlterarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    // Método para validar CPF
    private boolean isValidCPF(String cpf) {
        String cpfLimpo = cpf.replaceAll("[^\\d]", "");

        if (cpfLimpo.length() != 11 || cpfLimpo.matches("^(\\d)\\1{10}$")) {
            return false;
        }

        int soma = 0;
        int peso = 10;

        for (int i = 0; i < 9; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * peso--;
        }

        int digito1 = 11 - (soma % 11);
        digito1 = digito1 > 9 ? 0 : digito1;

        soma = 0;
        peso = 11;

        for (int i = 0; i < 10; i++) {
            soma += Character.getNumericValue(cpfLimpo.charAt(i)) * peso--;
        }

        int digito2 = 11 - (soma % 11);
        digito2 = digito2 > 9 ? 0 : digito2;

        return cpfLimpo.charAt(9) == (char) (digito1 + '0') && cpfLimpo.charAt(10) == (char) (digito2 + '0');
    }

    @GetMapping("/alterarCliente")
    public String mostrarFormularioAlteracao(Model model, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            List<Endereco> enderecosEntrega = cliente.getEnderecosEntrega() != null ? cliente.getEnderecosEntrega() : new ArrayList<>();
            model.addAttribute("cliente", cliente);
            model.addAttribute("enderecosEntrega", enderecosEntrega);
            return "alterarCliente";
        }
        model.addAttribute("mensagem", "Sessão expirada. Faça login novamente.");
        return "redirect:/loginCliente";
    }

    @PostMapping("/alterarCliente")
    public String alterarCliente(@ModelAttribute Cliente cliente,
                                 @RequestParam String senha,
                                 @RequestParam String confirmaSenha,
                                 Model model,
                                 HttpSession session) {

        Cliente clienteExistente = (Cliente) session.getAttribute("cliente");

        if (clienteExistente == null) {
            model.addAttribute("mensagem", "Sessão expirada. Por favor, faça login novamente.");
            return "redirect:/loginCliente";
        }

        // Validação dos campos obrigatórios
        if (cliente.getNome().isEmpty() || cliente.getEmail().isEmpty() ||
                cliente.getDataNascimento() == null || cliente.getCpf().isEmpty()) {
            model.addAttribute("mensagem", "Por favor, preencha todos os campos obrigatórios!");
            model.addAttribute("cliente", clienteExistente);
            return "alterarCliente";
        }

        // Valida o CPF
        String cpfLimpo = cliente.getCpf().replaceAll("[^\\d]", "");
        if (!isValidCPF(cpfLimpo)) {
            model.addAttribute("mensagem", "CPF inválido!");
            model.addAttribute("cliente", clienteExistente);
            return "alterarCliente";
        }

        // Verifica se as senhas coincidem
        if (!senha.isEmpty() && !senha.equals(confirmaSenha)) {
            model.addAttribute("mensagem", "As senhas não coincidem!");
            model.addAttribute("cliente", clienteExistente);
            return "alterarCliente";
        }

        // Atualiza os dados do cliente
        clienteExistente.setNome(cliente.getNome());
        clienteExistente.setEmail(cliente.getEmail());
        clienteExistente.setCpf(cliente.getCpf());
        clienteExistente.setDataNascimento(cliente.getDataNascimento());

        // Atualiza a senha se fornecida
        if (!senha.isEmpty()) {
            clienteExistente.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        }

        // Processa a lista de endereços
        List<Endereco> enderecosEntrega = cliente.getEnderecosEntrega() != null ? cliente.getEnderecosEntrega().stream()
                .filter(endereco -> endereco.getLogradouro() != null && !endereco.getLogradouro().isEmpty())
                .collect(Collectors.toList()) : new ArrayList<>();

        clienteExistente.setEnderecosEntrega(enderecosEntrega);
        clienteRepository.save(clienteExistente);

        session.setAttribute("cliente", clienteExistente);
        model.addAttribute("mensagem", "Informações do cliente atualizadas com sucesso!");
        model.addAttribute("cliente", clienteExistente);
        return "alterarCliente";
    }
}
