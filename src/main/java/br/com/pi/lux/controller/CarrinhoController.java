package br.com.pi.lux.controller;
import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
public class CarrinhoController {

    private List<ItemCarrinho> carrinho = new ArrayList<>();
    private double frete = 0.0;

    @Autowired
    private ProdutoRepository repository;

    // Classe interna para armazenar o produto e a quantidade no carrinho
    public static class ItemCarrinho {
        private Produto produto;
        private int quantidade;

        public ItemCarrinho(Produto produto, int quantidade) {
            this.produto = produto;
            this.quantidade = quantidade;
        }

        public Produto getProduto() {
            return produto;
        }

        public int getQuantidade() {
            return quantidade;
        }

        public void setQuantidade(int quantidade) {
            this.quantidade = quantidade;
        }
    }

    @PostMapping("/adicionarCarrinho")
    public String adicionarCarrinho(@RequestParam("id") Integer id,
                                    @RequestParam("quantidade") int quantidade,
                                    RedirectAttributes redirectAttributes) {
        Produto produto = repository.findById(id).orElse(null);

        if (produto != null) {
            Optional<ItemCarrinho> itemExistente = carrinho.stream()
                    .filter(item -> item.getProduto().getIdProduto() == id)
                    .findFirst();

            if (itemExistente.isPresent()) {
                itemExistente.get().setQuantidade(itemExistente.get().getQuantidade() + quantidade);
            } else {
                ItemCarrinho novoItem = new ItemCarrinho(produto, quantidade);
                carrinho.add(novoItem);
            }

            redirectAttributes.addFlashAttribute("mensagem", "Produto adicionado ao carrinho com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado!");
        }

        return "redirect:/carrinho";
    }

    @GetMapping("/carrinho")
    public String verCarrinho(Model model) {
        double totalCarrinho = carrinho.stream()
                .mapToDouble(item -> item.getProduto().getPreco() * item.getQuantidade())
                .sum();
        model.addAttribute("carrinho", carrinho);
        model.addAttribute("totalCarrinho", totalCarrinho);
        model.addAttribute("frete", frete);
        return "carrinho";
    }

    @PostMapping("/carrinho/frete")
    public String escolherFrete(@RequestParam("frete") double freteEscolhido, RedirectAttributes redirectAttributes) {
        this.frete = freteEscolhido;
        redirectAttributes.addFlashAttribute("mensagem", "Frete escolhido: R$ " + freteEscolhido);
        return "redirect:/carrinho";
    }

    @PostMapping("/atualizarQuantidade")
    public @ResponseBody Map<String, Object> atualizarQuantidade(@RequestParam("id") Integer id,
                                                                 @RequestParam("quantidade") int quantidade) {
        Optional<ItemCarrinho> itemCarrinho = carrinho.stream()
                .filter(item -> item.getProduto().getIdProduto() == id)
                .findFirst();

        Map<String, Object> response = new HashMap<>();
        if (itemCarrinho.isPresent()) {
            itemCarrinho.get().setQuantidade(quantidade);
            double subtotal = itemCarrinho.get().getProduto().getPreco() * quantidade;
            double totalCarrinho = carrinho.stream()
                    .mapToDouble(item -> item.getProduto().getPreco() * item.getQuantidade())
                    .sum();
            response.put("subtotal", subtotal);
            response.put("total", totalCarrinho + frete);
        } else {
            response.put("error", "Produto não encontrado no carrinho!");
        }

        return response;
    }

    @PostMapping("/removerCarrinho")
    public @ResponseBody Map<String, Object> removerCarrinho(@RequestParam("id") Integer id) {
        carrinho.removeIf(item -> item.getProduto().getIdProduto() == id);
        double totalCarrinho = carrinho.stream()
                .mapToDouble(item -> item.getProduto().getPreco() * item.getQuantidade())
                .sum();

        Map<String, Object> response = new HashMap<>();
        response.put("total", totalCarrinho + frete);
        return response;
    }

}
