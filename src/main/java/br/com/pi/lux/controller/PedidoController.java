package br.com.pi.lux.controller;

import br.com.pi.lux.model.*;
import br.com.pi.lux.service.PedidoService;
import br.com.pi.lux.service.EnderecoEntregaService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
                                  HttpSession session,
                                  Model model) {

        // Obtém o cliente da sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");
        if (cliente == null) {
            return "redirect:/loginCliente";  // Redireciona se o cliente não estiver logado
        }

        // Obtém o carrinho de compras da sessão
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");
        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho";  // Redireciona se o carrinho estiver vazio
        }

        // Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setFormaPagamento(formaPagamento);
        pedido.setFrete(freteEscolhido);
        pedido.setData(LocalDate.now());
        pedido.setStatus("Aguardando pagamento");

        // Calcula o total do pedido
        double totalCarrinho = carrinho.stream()
                .mapToDouble(ItemCarrinho::getTotal)
                .sum();
        pedido.setValorTotal(totalCarrinho + freteEscolhido);

        // Verifica o endereço de entrega
        EnderecoEntrega enderecoEntrega = enderecoEntregaService.buscarPorId(idEnderecoEntrega);
        if (enderecoEntrega != null) {
            pedido.setEnderecoEntrega(enderecoEntrega);
        } else {
            return "redirect:/erroEndereco";  // Redireciona se o endereço não for encontrado
        }

        // Adiciona os itens do carrinho ao pedido
        for (ItemCarrinho item : carrinho) {
            Produto produto = item.getProduto();
            ItemPedido itemPedido = new ItemPedido(pedido, produto, item.getQuantidade(), produto.getPreco());
            pedido.adicionarItem(itemPedido);
        }

        // Variáveis para mensagens e detalhes do pagamento
        String mensagemPagamento = null;
        String detalhePagamento = null;

        // Lógica de acordo com a forma de pagamento
        System.out.println("Forma de Pagamento recebida: " + formaPagamento);  // Debug log

        if ("cartao".equals(formaPagamento)) {
            // Mensagem específica para pagamento com cartão
            mensagemPagamento = "Pagamento realizado com cartão.";
            detalhePagamento = "Não aplicável"; // Pode-se adicionar detalhes como as últimas 4 cifras do cartão, se necessário
        } else if ("boleto".equals(formaPagamento)) {
            // Pagamento via boleto, sem necessidade de gerar UUID
            detalhePagamento = "Boleto gerado com sucesso.";
            mensagemPagamento = "Pagamento via boleto gerado com sucesso.";
        } else if ("pix".equals(formaPagamento)) {
            // Pagamento via Pix, sem necessidade de gerar UUID
            detalhePagamento = "Chave Pix gerada com sucesso.";
            mensagemPagamento = "Pagamento via Pix gerado com sucesso.";
        } else {
            return "redirect:/erroPagamento";  // Caso a forma de pagamento não seja reconhecida
        }

        // Salva o pedido no banco de dados
        pedidoService.finalizarPedido(pedido, freteEscolhido, formaPagamento, idEnderecoEntrega);

        // Limpa o carrinho da sessão após finalizar o pedido
        session.removeAttribute("carrinho");

        // Adiciona as informações ao modelo para a view
        model.addAttribute("pedido", pedido);
        model.addAttribute("mensagemPagamento", mensagemPagamento);
        model.addAttribute("detalhePagamento", detalhePagamento);
        model.addAttribute("enderecoEntrega", enderecoEntrega);

        // Redireciona para a página de "meus pedidos"
        return "redirect:/meusPedidos";
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

    @GetMapping("/pedido/{id}/detalhes")
    public String detalhesPedido(@PathVariable("id") int id, Model model) {
        Pedido pedido = pedidoService.buscarPedidoPorId(id);

        // Se o pedido não for encontrado, redireciona para a página de erro
        if (pedido == null) {
            return "error"; // Página de erro personalizada
        }

        // Adiciona o pedido ao modelo para ser usado no template
        model.addAttribute("pedido", pedido);

        // Retorna o template de detalhes do pedido
        return "pedidoDetalhes";
    }

    @GetMapping("/resumoPedido")
    public String exibirResumoPedido(Model model, HttpSession session) {
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

        // Cria o pedido temporário para mostrar o resumo
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente);
        pedido.setValorTotal(totalCarrinho);
        pedido.setFormaPagamento("");  // Forma de pagamento ainda não escolhida
        pedido.setStatus("Aguardando finalização"); // Pedido em estado intermediário
        pedido.setData(LocalDate.now()); // Define a data do pedido

        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", totalCarrinho);
        model.addAttribute("pedido", pedido);

        // Redireciona para a página de resumo do pedido
        return "resumoPedido";
    }



}
