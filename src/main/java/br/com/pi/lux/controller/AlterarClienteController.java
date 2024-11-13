package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.EnderecoFaturamento;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.repository.EnderecoEntregaRepository;
import br.com.pi.lux.repository.EnderecoFaturamentoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AlterarClienteController {

    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private EnderecoEntregaRepository enderecoEntregaRepository;

    @Autowired
    private EnderecoFaturamentoRepository enderecoFaturamentoRepository;


    // Exibe o formulário de alteração de cliente
    @GetMapping("/alterarCliente")
    @Transactional
    public String mostrarFormularioAlteracao(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            if (cliente.getEnderecosEntrega() != null) {
                model.addAttribute("enderecosEntrega", cliente.getEnderecosEntrega()); // Passa os endereços para o modelo
            }
            model.addAttribute("cliente", cliente);
            return "alterarCliente";  // Exibe a página com os dados do cliente e endereços
        }
        return "redirect:/loginCliente";  // Redireciona para o login se o cliente não estiver na sessão
    }

    // Salva as alterações no endereço de entrega
    // Processa o formulário de alteração de cliente
    @PostMapping("/alterarCliente")
    @Transactional
    public String salvarAlteracoesCliente(@ModelAttribute("cliente") Cliente clienteAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            // Atualiza as informações do cliente com os dados recebidos do formulário
            cliente.setNome(clienteAlterado.getNome());
            cliente.setEmail(clienteAlterado.getEmail());
            cliente.setCpf(clienteAlterado.getCpf());
            cliente.setDataNascimento(clienteAlterado.getDataNascimento());
            cliente.setGenero(clienteAlterado.getGenero());

            // Salva as alterações no banco de dados
            clienteRepository.save(cliente);  // Salva as mudanças feitas no cliente
            session.setAttribute("cliente", cliente);  // Atualiza a sessão com o cliente modificado

            return "redirect:/perfilCliente";  // Redireciona para o perfil após salvar
        }

        return "redirect:/loginCliente";  // Se o cliente não estiver na sessão, redireciona para o login
    }

    // Exibe o formulário para editar o endereço de faturamento
    @GetMapping("/editarEnderecoFaturamento")
    public String editarEnderecoFaturamento(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null && cliente.getEnderecoFaturamento() != null) {
            model.addAttribute("endereco", cliente.getEnderecoFaturamento());
            return "editarEnderecoFaturamento";  // Exibe a página para edição do endereço de faturamento
        }
        return "redirect:/loginCliente";  // Redireciona para o login se não encontrar o cliente ou o endereço
    }

    @GetMapping("/editarEnderecosEntrega/{id}")
    public String editarEnderecoEntrega(@PathVariable("id") int id, HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            EnderecoEntrega endereco = cliente.getEnderecosEntrega().stream()
                    .filter(e -> e.getIdEndereco() == id)
                    .findFirst()
                    .orElse(null);

            if (endereco != null) {
                model.addAttribute("endereco", endereco);  // Passa o endereço específico para o Thymeleaf
                return "editarEnderecosEntrega";  // Retorna a página de edição do endereço de entrega
            } else {
                System.out.println("Endereço não encontrado com ID: " + id);
            }
        }
        return "redirect:/loginCliente";
    }



    @PostMapping("/alterarEnderecoEntrega")
    @Transactional
    public String alterarEnderecoEntrega(@ModelAttribute("endereco") EnderecoEntrega enderecoAlterado, HttpSession session) {
        System.out.println("Endereço alterado recebido: " + enderecoAlterado);

        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            // Encontre o endereço de entrega que será alterado
            EnderecoEntrega endereco = cliente.getEnderecosEntrega().stream()
                    .filter(e -> e.getIdEndereco() == enderecoAlterado.getIdEndereco())
                    .findFirst()
                    .orElse(null);

            if (endereco != null) {
                // Atualiza o endereço com os novos dados
                endereco.setCep(enderecoAlterado.getCep());
                endereco.setLogradouro(enderecoAlterado.getLogradouro());
                endereco.setNumero(enderecoAlterado.getNumero());
                endereco.setComplemento(enderecoAlterado.getComplemento());
                endereco.setBairro(enderecoAlterado.getBairro());
                endereco.setCidade(enderecoAlterado.getCidade());
                endereco.setUf(enderecoAlterado.getUf());

                // Salva as alterações no banco de dados
                enderecoEntregaRepository.save(endereco);  // Salva o endereço modificado
                System.out.println("Endereço alterado e salvo: " + endereco);

                // Atualiza o cliente na sessão (apenas se necessário)
                session.setAttribute("cliente", cliente);

                // Redireciona para a página do perfil do cliente
                return "redirect:/perfilCliente";
            } else {
                System.out.println("Endereço não encontrado com ID: " + enderecoAlterado.getIdEndereco());
                return "redirect:/loginCliente";
            }
        }
        return "redirect:/loginCliente";
    }

    // Salva as alterações do endereço de faturamento
    @PostMapping("/alterarEnderecoFaturamento")
    @Transactional
    public String alterarEnderecoFaturamento(@ModelAttribute("endereco") EnderecoFaturamento enderecoAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            // Encontra o endereço de faturamento e atualiza os dados (sem o tipoEndereco)
            EnderecoFaturamento endereco = cliente.getEnderecoFaturamento();

            if (endereco != null) {
                endereco.setCep(enderecoAlterado.getCep());
                endereco.setLogradouro(enderecoAlterado.getLogradouro());
                endereco.setNumero(enderecoAlterado.getNumero());
                endereco.setComplemento(enderecoAlterado.getComplemento());
                endereco.setBairro(enderecoAlterado.getBairro());
                endereco.setCidade(enderecoAlterado.getCidade());
                endereco.setUf(enderecoAlterado.getUf());

                enderecoFaturamentoRepository.save(endereco); // Salva as alterações

                session.setAttribute("cliente", cliente); // Atualiza a sessão

                return "redirect:/perfilCliente"; // Redireciona
            }
        }
        return "redirect:/loginCliente"; // Caso o cliente não esteja na sessão
    }



}
