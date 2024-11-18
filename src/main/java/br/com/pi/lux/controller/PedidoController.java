package br.com.pi.lux.controller;

import br.com.pi.lux.model.*;
import br.com.pi.lux.service.PedidoService;
import br.com.pi.lux.service.EnderecoEntregaService;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;

@Controller
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private EnderecoEntregaService enderecoEntregaService;

    @GetMapping("/finalizarCompra")
    public String exibirPaginaFinalizacao(Model model, HttpSession session) {
        // Verifica se o cliente está logado
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";  // Redireciona para login se cliente não estiver logado
        }

        // Obtém o carrinho da sessão
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho";  // Redireciona se o carrinho estiver vazio
        }

        // Calcula o total do carrinho
        double totalCarrinho = carrinho.stream()
                .mapToDouble(ItemCarrinho::getTotal)
                .sum();

        // Cria o pedido e associa o cliente a ele
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setValorTotal(totalCarrinho);
        pedido.setFormaPagamento("");  // Forma de pagamento ainda não escolhida

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", totalCarrinho);
        model.addAttribute("pedido", pedido);

        return "finalizarCompra";
    }

    @PostMapping("/finalizarPedido")
    public String finalizarPedido(@RequestParam("frete") double freteEscolhido,
                                  @RequestParam("formaPagamento") String formaPagamento,
                                  @RequestParam("enderecoEntrega") int idEnderecoEntrega,
                                  @RequestParam(value = "numeroCartao", required = false) String numeroCartao,
                                  @RequestParam(value = "validadeCartao", required = false) String validadeCartao,
                                  @RequestParam(value = "codigoSeguranca", required = false) String codigoSeguranca,
                                  HttpSession session,
                                  Model model) {

        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";
        }

        // Obtém o carrinho da sessão
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho";
        }

        // Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setFrete(freteEscolhido);

        if (pedido.getData() == null) {
            pedido.setData(LocalDate.now());
        }

        if (pedido.getStatus() == null) {
            pedido.setStatus("Aguardando pagamento");
        }

        // Calcula o valor total
        double totalCarrinho = carrinho.stream()
                .mapToDouble(ItemCarrinho::getTotal)
                .sum();
        pedido.setValorTotal(totalCarrinho + freteEscolhido);

        // Busca o endereço de entrega
        EnderecoEntrega enderecoEntrega = enderecoEntregaService.buscarPorId(idEnderecoEntrega);
        if (enderecoEntrega != null) {
            pedido.setEnderecoEntrega(enderecoEntrega);
        } else {
            return "redirect:/erroEndereco";
        }

        // Adiciona os itens do carrinho ao pedido
        for (ItemCarrinho item : carrinho) {
            Produto produto = item.getProduto();
            ItemPedido itemPedido = new ItemPedido(pedido, produto, item.getQuantidade(), produto.getPreco());
            pedido.adicionarItem(itemPedido);
        }

        // Lógica para pagamento
        if (formaPagamento.equals("cartao")) {
            // Processamento para pagamento com cartão
            String ultimoCartao = numeroCartao.substring(numeroCartao.length() - 4);
            model.addAttribute("numeroCartao", ultimoCartao);  // Exibe os últimos 4 dígitos do cartão
        } else if (formaPagamento.equals("boleto")) {
            // Gerar código de boleto fictício
            String codigoBoleto = gerarCodigoBoleto();
            model.addAttribute("numeroCartao", "Pagamento via boleto");
            model.addAttribute("codigoBoleto", codigoBoleto);  // Passa o código do boleto para o modelo
        } else {
            model.addAttribute("numeroCartao", "Não aplicável");
        }

        // Finaliza o pedido
        pedidoService.finalizarPedido(pedido, freteEscolhido, formaPagamento, idEnderecoEntrega);
        session.removeAttribute("carrinho");

        model.addAttribute("pedido", pedido);
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("numeroCartao", "Não aplicável");
        model.addAttribute("enderecoEntrega", enderecoEntrega);

        return "redirect:/meusPedidos";
    }

    // Método fictício para gerar código de boleto
    private String gerarCodigoBoleto() {
        // Aqui você poderia gerar um código de boleto ou link de pagamento real
        return "BOLETO-1234567890";  // Exemplo de código fictício
    }

    @GetMapping("/meusPedidos")
    public String exibirMeusPedidos(HttpSession session, Model model) {
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            return "redirect:/loginCliente";
        }

        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(cliente);

        if (pedidos.isEmpty()) {
            model.addAttribute("mensagem", "Você não possui pedidos.");
        }

        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);

        return "meusPedidos";  // Página que exibe os pedidos
    }
}
