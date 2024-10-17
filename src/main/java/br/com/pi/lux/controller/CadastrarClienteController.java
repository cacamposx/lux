package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.Endereco;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.service.CepService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Optional;

@Controller
public class CadastrarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CepService cepService;

    @GetMapping("/cadastrarCliente")
    public String mostrarFormularioCadastro(Model model) {
        Cliente cliente = new Cliente();
        Endereco enderecoEntrega = new Endereco();
        cliente.getEnderecosEntrega().add(enderecoEntrega);
        model.addAttribute("cliente", cliente);
        return "cadastrarCliente";
    }

    @PostMapping("/cadastrarCliente")
    public String inserirCliente(@ModelAttribute Cliente cliente,
                                 @RequestParam String senha,
                                 @RequestParam String confirmaSenha,
                                 Model model) {

        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("mensagem", "As senhas não coincidem!");
            return "cadastrarCliente";
        }

        if (!isValidCPF(cliente.getCpf())) {
            model.addAttribute("mensagem", "CPF inválido!");
            return "cadastrarCliente";
        }

        if (!isValidNome(cliente.getNome())) {
            model.addAttribute("mensagem", "O nome deve conter no mínimo duas palavras e cada palavra deve ter pelo menos três letras!");
            return "cadastrarCliente";
        }

        if (!cepService.isValidCep(cliente.getEnderecoFaturamento().getCep())) {
            model.addAttribute("mensagem", "CEP inválido!");
            return "cadastrarCliente";
        }

        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteExistente.isPresent()) {
            model.addAttribute("mensagem", "O email já está cadastrado!");
            return "cadastrarCliente";
        }

        String cpfLimpo = cliente.getCpf().replaceAll("[^\\d]", "");
        Optional<Cliente> cpfExistente = clienteRepository.findByCpf(cpfLimpo);
        if (cpfExistente.isPresent()) {
            model.addAttribute("mensagem", "O CPF já está cadastrado!");
            return "cadastrarCliente";
        }

        cliente.setSenha(BCrypt.hashpw(senha, BCrypt.gensalt()));
        cliente.setStatus(true);
        cliente.setCpf(cpfLimpo);

        // Definir relacionamentos bidirecionais
        for (Endereco endereco : cliente.getEnderecosEntrega()) {
            endereco.setCliente(cliente);
        }
        cliente.getEnderecoFaturamento().setCliente(cliente);

        clienteRepository.save(cliente);

        model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
        return "redirect:/loginCliente";
    }

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

    private boolean isValidNome(String nome) {
        String[] palavras = nome.trim().split("\\s+");
        if (palavras.length < 2) {
            return false;
        }
        for (String palavra : palavras) {
            if (palavra.length() < 3) {
                return false;
            }
        }
        return true;
    }
}

