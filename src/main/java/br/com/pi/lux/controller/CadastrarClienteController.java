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

@Controller
public class CadastrarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CepService cepService;

    @GetMapping("/cadastrarCliente")
    public String mostrarFormularioCadastro(Model model) {
        Cliente cliente = new Cliente();

        // Criar o endereço de faturamento e garantir que tenha tipo FATURAMENTO
        EnderecoFaturamento enderecoFaturamento = new EnderecoFaturamento();
        enderecoFaturamento.setTipoEndereco(TipoEndereco.FATURAMENTO);  // Usando o enum comum
        cliente.setEnderecoFaturamento(enderecoFaturamento);

        // Criar o endereço de entrega e garantir que tenha tipo ENTREGA
        EnderecoEntrega enderecoEntrega = new EnderecoEntrega();
        enderecoEntrega.setTipoEndereco(TipoEndereco.ENTREGA);  // Usando o enum comum
        cliente.getEnderecosEntrega().add(enderecoEntrega);

        model.addAttribute("cliente", cliente);
        return "cadastrarCliente";
    }


    @PostMapping("/cadastrarCliente")
    public String inserirCliente(@ModelAttribute Cliente cliente,
                                 @RequestParam String senha,
                                 @RequestParam String confirmaSenha,
                                 Model model) {
        // Validação das senhas
        if (!senha.equals(confirmaSenha)) {
            model.addAttribute("mensagem", "As senhas não coincidem!");
            return "cadastrarCliente";
        }

        // Criptografando a senha
        String senhaCriptografada = BCrypt.hashpw(senha, BCrypt.gensalt());
        cliente.setSenha(senhaCriptografada);

        // Verifique se o status está null e defina um valor padrão
        if (cliente.getStatus() == null) {
            cliente.setStatus("ATIVO");  // Defina um status padrão
        }

        // Verificar todos os endereços de entrega e garantir que cada um tem o tipo de endereço definido
        if (cliente.getEnderecosEntrega() != null) {
            for (EnderecoEntrega endereco : cliente.getEnderecosEntrega()) {
                if (endereco.getTipoEndereco() == null) {
                    endereco.setTipoEndereco(TipoEndereco.ENTREGA);  // Garantir que o tipo está setado
                }
            }
        }

        // Certifique-se de que o tipo de endereço de faturamento está definido
        if (cliente.getEnderecoFaturamento() != null) {
            EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();
            if (enderecoFaturamento.getTipoEndereco() == null) {
                enderecoFaturamento.setTipoEndereco(TipoEndereco.FATURAMENTO);  // Garantir que o tipo está setado
            }
        }

        // Salvando o cliente no banco de dados
        clienteRepository.save(cliente);

        model.addAttribute("mensagem", "Cliente cadastrado com sucesso!");
        return "redirect:/loginCliente";
    }






    @PostMapping("/preencherEndereco")
    @ResponseBody
    public EnderecoEntrega preencherEndereco(@RequestParam String cep) {
        if (cepService.isValidCep(cep)) {
            CepService.ViaCepResponse viaCepResponse = cepService.buscarEnderecoPorCep(cep);
            if (viaCepResponse != null) {
                EnderecoEntrega enderecoEntrega = cepService.converterParaEndereco(viaCepResponse);
                return enderecoEntrega;  // Retorna o objeto EnderecoEntrega como JSON
            } else {
                return null;  // Caso o CEP não seja encontrado, retorna null
            }
        } else {
            return null;  // Caso o CEP não seja válido, retorna null
        }
    }
}


