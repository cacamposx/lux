package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.EnderecoEntrega;
import br.com.pi.lux.model.EnderecoFaturamento;
import br.com.pi.lux.model.enums.TipoEndereco;
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

    @PostMapping("/alterarCliente")
    @Transactional
    public String salvarAlteracoesCliente(@ModelAttribute("cliente") Cliente clienteAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            // Buscar o cliente no banco de dados para garantir que o contexto de persistência esteja correto
            cliente = clienteRepository.findById(cliente.getIdCliente()).orElse(null);

            if (cliente != null) {
                // Atualiza os dados do cliente
                cliente.setNome(clienteAlterado.getNome());
                cliente.setEmail(clienteAlterado.getEmail());
                cliente.setCpf(clienteAlterado.getCpf());
                cliente.setDataNascimento(clienteAlterado.getDataNascimento());
                cliente.setGenero(clienteAlterado.getGenero());

                // Salva as alterações no banco de dados
                clienteRepository.save(cliente);

                // Atualiza a sessão com os dados alterados
                session.setAttribute("cliente", cliente);

                // Redireciona para o perfil do cliente
                return "redirect:/perfilCliente";
            }
        }
        return "redirect:/loginCliente";  // Se o cliente não estiver na sessão
    }


    // Adicionar novo endereço de entrega
    @GetMapping("/adicionarEnderecoEntrega")
    public String adicionarEnderecoEntrega(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            model.addAttribute("endereco", new EnderecoEntrega());  // Cria um novo objeto para o formulário
            return "editarEnderecosEntrega";  // Exibe o formulário de adição
        }
        return "redirect:/loginCliente";  // Redireciona se não houver cliente na sessão
    }

    // Exibe o formulário para editar o endereço de entrega
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

    // Exibe o formulário de alteração do endereço de faturamento
    @GetMapping("/editarEnderecoFaturamento/{id}")
    public String editarEnderecoFaturamento(@PathVariable("id") int id, HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();

            if (enderecoFaturamento != null && enderecoFaturamento.getIdEnderecoFaturamento() == id) {
                model.addAttribute("endereco", enderecoFaturamento);
                return "editarEnderecoFaturamento";  // Exibe o formulário para editar o endereço de faturamento
            } else {
                // Se o endereço de faturamento não for encontrado
                return "redirect:/perfilCliente";  // Redireciona para o perfil do cliente
            }
        }
        return "redirect:/loginCliente";  // Se o cliente não estiver na sessão
    }




    @PostMapping("/alterarEnderecoEntrega")
    @Transactional
    public String alterarEnderecoEntrega(@ModelAttribute("endereco") EnderecoEntrega enderecoAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente != null) {
            // Buscando o cliente novamente no banco de dados para garantir que ele esteja no contexto de persistência
            cliente = clienteRepository.findById(cliente.getIdCliente()).orElse(null);

            if (cliente != null) {
                // Verifica se o endereço já existe ou é um novo
                EnderecoEntrega enderecoExistente = null;
                if (enderecoAlterado.getIdEndereco() != 0) {
                    // Se o endereço já existir, apenas atualiza
                    enderecoExistente = cliente.getEnderecosEntrega().stream()
                            .filter(e -> e.getIdEndereco() == enderecoAlterado.getIdEndereco())
                            .findFirst()
                            .orElse(null);
                }

                if (enderecoExistente != null) {
                    // Atualiza o endereço existente
                    enderecoExistente.setCep(enderecoAlterado.getCep());
                    enderecoExistente.setLogradouro(enderecoAlterado.getLogradouro());
                    enderecoExistente.setNumero(enderecoAlterado.getNumero());
                    enderecoExistente.setComplemento(enderecoAlterado.getComplemento());
                    enderecoExistente.setBairro(enderecoAlterado.getBairro());
                    enderecoExistente.setCidade(enderecoAlterado.getCidade());
                    enderecoExistente.setUf(enderecoAlterado.getUf());
                } else {
                    // Verificar duplicidade com base em atributos relevantes (como logradouro, número, etc.)
                    boolean enderecoDuplicado = cliente.getEnderecosEntrega().stream()
                            .anyMatch(e -> e.getCep().equals(enderecoAlterado.getCep()) &&
                                    e.getLogradouro().equals(enderecoAlterado.getLogradouro()) &&
                                    e.getNumero().equals(enderecoAlterado.getNumero()));

                    if (!enderecoDuplicado) {
                        // Adiciona um novo endereço à lista se não for duplicado
                        enderecoAlterado.setCliente(cliente); // Define o cliente no novo endereço
                        enderecoAlterado.setTipoEndereco(TipoEndereco.ENTREGA);  // Aqui atribuímos o tipo de endereço como ENTREGA

                        cliente.getEnderecosEntrega().add(enderecoAlterado);
                    } else {
                        // Se o endereço for duplicado, você pode lançar um erro ou apenas ignorar a adição
                        return "redirect:/erroEnderecoDuplicado";  // Um exemplo de redirecionamento para uma página de erro
                    }
                }

                // Salva o cliente e os endereços juntos
                clienteRepository.save(cliente); // Salva o cliente e seus endereços (não precisa salvar o endereço individualmente)

                // Atualiza a sessão com o cliente modificado
                session.setAttribute("cliente", cliente);

                // Redireciona para a página do perfil do cliente
                return "redirect:/perfilCliente";
            }
        }
        return "redirect:/loginCliente";
    }

    // Método para salvar as alterações no endereço de faturamento
    @PostMapping("/alterarEnderecoFaturamento")
    @Transactional
    public String alterarEnderecoFaturamento(@ModelAttribute("endereco") EnderecoFaturamento enderecoAlterado, HttpSession session) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente != null) {
            EnderecoFaturamento enderecoFaturamento = cliente.getEnderecoFaturamento();

            if (enderecoFaturamento != null) {
                // Atualiza os dados do endereço de faturamento
                enderecoFaturamento.setCep(enderecoAlterado.getCep());
                enderecoFaturamento.setLogradouro(enderecoAlterado.getLogradouro());
                enderecoFaturamento.setNumero(enderecoAlterado.getNumero());
                enderecoFaturamento.setComplemento(enderecoAlterado.getComplemento());
                enderecoFaturamento.setBairro(enderecoAlterado.getBairro());
                enderecoFaturamento.setCidade(enderecoAlterado.getCidade());
                enderecoFaturamento.setUf(enderecoAlterado.getUf());

                enderecoFaturamentoRepository.save(enderecoFaturamento);  // Salva as alterações

                session.setAttribute("cliente", cliente);  // Atualiza a sessão com os dados alterados do cliente
                return "redirect:/perfilCliente";  // Redireciona para o perfil do cliente
            }
        }
        return "redirect:/loginCliente";  // Se o cliente não estiver na sessão
    }
}
