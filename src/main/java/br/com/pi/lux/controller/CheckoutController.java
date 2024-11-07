package br.com.pi.lux.controller;

import br.com.pi.lux.model.Cliente;
import br.com.pi.lux.model.Endereco;
import br.com.pi.lux.controller.ItemCarrinho;
import br.com.pi.lux.model.ItemPedido;
import br.com.pi.lux.model.Pedido;
import br.com.pi.lux.repository.ClienteRepository;
import br.com.pi.lux.repository.PedidoRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import java.time.LocalDate;
import java.util.List;

@Controller
public class CheckoutController {

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private ClienteRepository clienteRepository;

    // Método GET para exibir o formulário de checkout
    @GetMapping("/finalizarCompra")
    public String finalizarCompra(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";  // Redireciona se o cliente não estiver logado
        }

        // Verifica se o carrinho está vazio
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("mensagemErro", "Seu carrinho está vazio!");
            return "redirect:/";  // Redireciona para a página inicial se o carrinho estiver vazio
        }

        // Verifica se o cliente tem endereços cadastrados
        if (cliente.getEnderecosEntrega() == null || cliente.getEnderecosEntrega().isEmpty()) {
            model.addAttribute("mensagemErro", "Você não tem endereços cadastrados.");
            return "redirect:/adicionarEndereco";  // Redireciona para adicionar um novo endereço
        } else {
            model.addAttribute("enderecos", cliente.getEnderecosEntrega());
        }

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("cliente", cliente);
        return "checkout";  // Exibe o checkout
    }

    // Método POST para processar a seleção de endereço e forma de pagamento
    @PostMapping("/finalizarCompra")
    public String escolherEnderecoEntrega(HttpSession session, Model model, Integer idEndereco, String formaPagamento,
                                          String numeroCartao, String nomeCartao, String codigoVerificador,
                                          String dataVencimento, Integer parcelas) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";  // Redireciona se o cliente não estiver logado
        }

        // Validações de endereço e forma de pagamento
        Endereco enderecoEscolhido = cliente.getEnderecosEntrega().stream()
                .filter(e -> e.getIdEndereco() == idEndereco)
                .findFirst().orElse(null);
        if (enderecoEscolhido == null) {
            model.addAttribute("mensagemErro", "Endereço inválido.");
            return "redirect:/finalizarCompra";
        }
        if (formaPagamento == null || formaPagamento.isEmpty()) {
            model.addAttribute("mensagemErro", "Você deve escolher uma forma de pagamento.");
            return "redirect:/finalizarCompra";
        }

        // Validação adicional se a forma de pagamento for cartão
        if ("Cartão de Crédito".equals(formaPagamento)) {
            if (numeroCartao == null || nomeCartao == null || codigoVerificador == null || dataVencimento == null || parcelas == null) {
                model.addAttribute("mensagemErro", "Preencha todos os campos do cartão de crédito.");
                return "redirect:/finalizarCompra";
            }
        }

        // Criação do pedido e salvamento no banco de dados
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("mensagemErro", "Seu carrinho está vazio!");
            return "redirect:/";
        }

        List<ItemPedido> itensPedido = carrinho.stream()
                .map(itemCarrinho -> new ItemPedido(
                        null, itemCarrinho.getProduto(), itemCarrinho.getQuantidade(), itemCarrinho.getProduto().getPreco()))
                .toList();

        Pedido pedido = new Pedido(cliente, enderecoEscolhido, itensPedido, "Em Andamento", formaPagamento, LocalDate.now(), 0);
        pedido.calcularValorTotal();

        for (ItemPedido itemPedido : itensPedido) {
            itemPedido.setPedido(pedido);
        }

        pedidoRepository.save(pedido);
        session.setAttribute("carrinho", null);

        model.addAttribute("pedido", pedido);
        return "pedidoConfirmado";
    }


    // Método para exibir o formulário de adicionar um novo endereço
    @GetMapping("/adicionarEndereco")
    public String adicionarEndereco(HttpSession session, Model model) {
        return "adicionarEndereco";  // Redireciona para a página de adicionar endereço
    }

    // Método para salvar o novo endereço
    @PostMapping("/salvarEndereco")
    public String salvarEndereco(HttpSession session, Endereco endereco, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";  // Redireciona se o cliente não estiver logado
        }

        // Associa o novo endereço ao cliente
        cliente.addEnderecoEntrega(endereco);

        // Salva o cliente com o novo endereço
        clienteRepository.save(cliente);

        return "redirect:/finalizarCompra";  // Redireciona para a página de checkout
    }

    @GetMapping("/resumoPedido")
    public String exibirResumoPedido(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente"; // Redireciona se o cliente não estiver logado
        }

        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            model.addAttribute("mensagemErro", "Seu carrinho está vazio!");
            return "redirect:/"; // Redireciona para a página inicial se o carrinho estiver vazio
        }

        Endereco enderecoEscolhido = (Endereco) session.getAttribute("enderecoEscolhido");
        String formaPagamento = (String) session.getAttribute("formaPagamento");

        // Calcula o total do pedido (incluindo frete se aplicável)
        double totalGeral = carrinho.stream()
                .mapToDouble(item -> item.getProduto().getPreco() * item.getQuantidade())
                .sum();
        double frete = 20.00; // Exemplo de frete fixo
        totalGeral += frete;

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("cliente", cliente);
        model.addAttribute("enderecoEntrega", enderecoEscolhido);
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("frete", frete);
        model.addAttribute("totalGeral", totalGeral);

        return "resumoPedido"; // Exibe o resumo do pedido
    }


}
