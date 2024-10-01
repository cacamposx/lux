package br.com.pi.lux.controller;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.model.ProdutoImagem;
import br.com.pi.lux.repository.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Base64;
import java.util.List;

@Controller
public class HomeEcommerceController {

    @Autowired
    private ProdutoRepository repository;

    @GetMapping("/homeEcommerce")
    public String homeEcommerce(
            Model model,
            @RequestParam(required = false) String nome) {

        List<Produto> produtos;
        if (nome != null && !nome.isEmpty()) {
            produtos = repository.findByNomeContainingIgnoreCase(nome);
        } else {
            produtos = repository.findAll(); //
        }

        Page<Produto> produtosPage = new PageImpl<>(produtos);

        produtos.forEach(produto -> {
            if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
                ProdutoImagem imagemPrincipal = produto.getImagemPrincipal();
                if (imagemPrincipal != null && imagemPrincipal.getImagem() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(imagemPrincipal.getImagem());
                    produto.setBase64Image(base64Image);
                }
            }
        });

        model.addAttribute("produtos", produtosPage.getContent());

        return "homeEcommerce";
    }
}
