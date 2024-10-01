package br.com.pi.lux.controller;
import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    @PostMapping("/removerCarrinho")
    public String removerCarrinho(@RequestParam("id") Integer id, RedirectAttributes redirectAttributes) {
        carrinho.removeIf(item -> item.getProduto().getIdProduto() == id);
        redirectAttributes.addFlashAttribute("mensagem", "Produto removido do carrinho com sucesso!");

        return "redirect:/carrinho";
    }

    @PostMapping("/atualizarQuantidade")
    public String atualizarQuantidade(@RequestParam("id") Integer id,
                                      @RequestParam("quantidade") int quantidade,
                                      RedirectAttributes redirectAttributes) {
        Optional<ItemCarrinho> itemCarrinho = carrinho.stream()
                .filter(item -> item.getProduto().getIdProduto() == id)
                .findFirst();

        if (itemCarrinho.isPresent()) {
            itemCarrinho.get().setQuantidade(quantidade);
            redirectAttributes.addFlashAttribute("mensagem", "Quantidade atualizada com sucesso!");
        } else {
            redirectAttributes.addFlashAttribute("mensagemErro", "Produto não encontrado no carrinho!");
        }

        return "redirect:/carrinho";
    }

}
