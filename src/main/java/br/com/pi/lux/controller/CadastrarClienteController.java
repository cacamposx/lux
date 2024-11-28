package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.EnderecoFaturamento;
import br.com.pi.lux.model.enums.TipoEndereco;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.service.CepService;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@Controller
public class CadastrarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CepService cepService;

    @GetMapping("/cadastrarCliente")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("cliente",new Cliente());
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

        // Criptografando a senha
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        cliente.setSenha(senhaCriptografada);

        if (!isValidCPF(cliente.getCpf())) {
            model.addAttribute("mensagem", "CPF inválido!");
            return "cadastrarCliente";
        }

        Optional<Cliente> clienteExistente = clienteRepository.findByEmail(cliente.getEmail());
        if (clienteExistente.isPresent()) {
            model.addAttribute("mensagem", "O email já está cadastrado!");
            return "cadastrarCliente";
        }

        Optional<Cliente> cpfExistente = clienteRepository.findByCpf(cliente.getCpf());
        if (cpfExistente.isPresent()) {
            model.addAttribute("mensagem", "O CPF já está cadastrado!");
            return "cadastrarCliente";
        }


        // Verifique se o status está null e defina um valor padrão
        if (cliente.getStatus() == null) {
            cliente.setStatus("ATIVO");
        }

        // Associar os endereços de entrega e faturamento ao cliente
        if (cliente.getEnderecosEntrega() != null) {
            for (EnderecoEntrega endereco : cliente.getEnderecosEntrega()) {
                if (endereco.getTipoEndereco() == null) {
                    endereco.setTipoEndereco(TipoEndereco.ENTREGA);  // Garantir que o tipo esteja setado
                }
                endereco.setCliente(cliente);  // Garantir que o cliente está associado
            }
        }

        if (cliente.getEnderecoFaturamento() != null) {
            EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();
            if (enderecoFaturamento.getTipoEndereco() == null) {
                enderecoFaturamento.setTipoEndereco(TipoEndereco.FATURAMENTO);
            }
            enderecoFaturamento.setCliente(cliente);  // Garantir que o cliente está associado
        }

        // Salvar o cliente e os endereços no banco
        clienteRepository.save(cliente);

        model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
        return "redirect:/loginCliente";
    }
    private boolean isValidCPF(String cpf) {
        String cpfLimpo = cpf.replaceAll("\\D", "");

        if (cpfLimpo.length() != 11 || cpfLimpo.matches("^(\\D)\\1{10}$")) {
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


    @PostMapping("/preencherEndereco")
    @ResponseBody
    public EnderecoEntrega preencherEndereco(@RequestParam String cep) {
        if (cepService.isValidCep(cep)) {
            CepService.ViaCepResponse viaCepResponse = cepService.buscarEnderecoPorCep(cep);
            if (viaCepResponse != null) {
                return cepService.converterParaEndereco(viaCepResponse);  // Retorna o objeto EnderecoEntrega como JSON
            } else {
                return null;  // Caso o CEP não seja encontrado, retorna null
            }
        } else {
            return null;  // Caso o CEP não seja válido, retorna null
        }
    }
}


