package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.repository.ProdutoRepository;

@Controller
public class CadastrarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/cadastrarProd")
    public String mostrarFormularioCadastro(Model model) {
        model.addAttribute("produto", new Produto());
        return "cadastrarProd";
    }

    @PostMapping("/cadastrarProd")
    public String inserirProduto(
            @RequestParam String nome,
            @RequestParam double preco,
            @RequestParam int quantidade,
            @RequestParam String descricao,
            @RequestParam double avaliacao,
            Model model) {

        Produto produto = new Produto();
        produto.setNome(nome);
        produto.setPreco(preco);
        produto.setQuantidade(quantidade);
        produto.setDescricao(descricao);
        produto.setAvaliacao(avaliacao);
        produto.setStatus(true);

        repository.save(produto);

        return "redirect:/listarProd";
    }
}
