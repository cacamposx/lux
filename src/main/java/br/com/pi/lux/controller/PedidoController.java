package br.com.pi.lux.controller;

import br.com.pi.lux.model.*;
import br.com.pi.lux.service.PedidoService;
import br.com.pi.lux.service.EnderecoEntregaService; // Supondo que você tenha um serviço para buscar o EnderecoEntrega
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
            // Se o cliente não estiver logado, redireciona para o login
            return "redirect:/loginCliente";
        }

        // Obtém o carrinho da sessão com segurança
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

        // Verifica se o carrinho não é nulo e não está vazio
        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho"; // Redireciona se o carrinho estiver vazio ou não existir
        }

        // Calcula o total do carrinho
        double totalCarrinho = carrinho.stream()
                .mapToDouble(ItemCarrinho::getTotal)
                .sum();

        // Cria o pedido e associa o cliente a ele
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente); // Aqui associamos o cliente ao pedido
        pedido.setValorTotal(totalCarrinho); // Define o valor total do pedido
        pedido.setFormaPagamento(""); // A forma de pagamento será escolhida no formulário

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
        // Obtém o cliente da sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            // Se o cliente não estiver logado, redireciona para a página de login
            return "redirect:/loginCliente";
        }

        // Obtém o carrinho da sessão
        List<ItemCarrinho> carrinho = (List<ItemCarrinho>) session.getAttribute("carrinho");

        if (carrinho == null || carrinho.isEmpty()) {
            return "redirect:/carrinho"; // Se o carrinho estiver vazio, redireciona para o carrinho
        }

        // Cria o pedido
        Pedido pedido = new Pedido();
        pedido.setCliente(cliente); // Atribui o cliente ao pedido
        pedido.setFormaPagamento(formaPagamento);
        pedido.setFrete(freteEscolhido);

        if (pedido.getData() == null) {
            pedido.setData(LocalDate.now()); // Define a data atual se estiver nula
        }

        if (pedido.getStatus() == null) {
            pedido.setStatus("Aguardando pagamento"); // Definindo o status inicial
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
            pedido.adicionarItem(itemPedido); // Adiciona o item ao pedido
        }

        // Para qualquer forma de pagamento (cartão, boleto, pix), o pedido será finalizado
        if (formaPagamento.equals("cartao")) {
            // Se o pagamento for com cartão, armazena as últimas 4 cifras do cartão para exibir na página de resumo
            String ultimoCartao = numeroCartao.substring(numeroCartao.length() - 4);
            model.addAttribute("numeroCartao", ultimoCartao); // Apenas os últimos 4 números do cartão
        } else {
            // Se o pagamento for com boleto ou pix, não há necessidade de mais dados
            model.addAttribute("numeroCartao", "Não aplicável");
        }

        // Chama o método correto de finalizarPedido passando os parâmetros necessários
        pedidoService.finalizarPedido(pedido, freteEscolhido, formaPagamento, idEnderecoEntrega);

        // Limpa o carrinho da sessão
        session.removeAttribute("carrinho");

        // Passa o pedido para a página de resumo
        model.addAttribute("pedido", pedido);
        model.addAttribute("formaPagamento", formaPagamento);
        model.addAttribute("numeroCartao", "Não aplicável"); // Sempre "Não aplicável" para formas de pagamento que não sejam cartão
        model.addAttribute("enderecoEntrega", enderecoEntrega); // Envia o endereço escolhido

        // Redireciona para a página de resumo
        return "redirect:/meusPedidos";
    }


    @GetMapping("/meusPedidos")
    public String exibirMeusPedidos(HttpSession session, Model model) {
        // Obtém o cliente da sessão
        Cliente cliente = (Cliente) session.getAttribute("cliente");

        if (cliente == null) {
            // Se o cliente não estiver logado, redireciona para o login
            return "redirect:/loginCliente";
        }

        // Busca todos os pedidos do cliente
        List<Pedido> pedidos = pedidoService.buscarPedidosPorCliente(cliente);

        // Adiciona os pedidos ao modelo para exibição na página
        model.addAttribute("pedidos", pedidos);
        model.addAttribute("cliente", cliente);

        // Log para verificar se os pedidos estão sendo encontrados
        System.out.println("Pedidos encontrados: " + pedidos.size());

        // Redireciona para a página que lista os pedidos
        return "meusPedidos";  // O nome da página Thymeleaf que exibe os pedidos
    }





}
