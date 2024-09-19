package br.com.pi.lux.controller;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AlterarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/alterarProd")
    public String mostrarFormularioAlteracao(@RequestParam("idProduto") int idProduto, Model model) {
        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            model.addAttribute("produto", produtoOptional.get());
            return "alterarProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "redirect:/listarProd";
        }
    }

    @PostMapping("/alterarProd")
    public String alterarProduto(@RequestParam int idProduto,
                                 @RequestParam String nome,
                                 @RequestParam double preco,
                                 @RequestParam int quantidade,
                                 @RequestParam String descricao,
                                 @RequestParam double avaliacao,
                                 @RequestParam boolean status,
                                 Model model) {

        Optional<Produto> produtoOptional = repository.findById(idProduto);
        if (produtoOptional.isPresent()) {
            Produto produto = produtoOptional.get();
            produto.setNome(nome);
            produto.setPreco(preco);
            produto.setQuantidade(quantidade);
            produto.setDescricao(descricao);
            produto.setAvaliacao(avaliacao);
            produto.setStatus(status);

            repository.save(produto);

            model.addAttribute("mensagem", "Produto alterado com sucesso!");
            return "redirect:/listarProd";
        } else {
            model.addAttribute("mensagem", "Produto não encontrado!");
            return "alterarProd";
        }
    }
}
