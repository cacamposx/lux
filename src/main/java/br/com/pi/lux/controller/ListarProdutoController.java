package br.com.pi.lux.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import br.com.pi.lux.model.Produto;
import br.com.pi.lux.model.ProdutoImagem;
import br.com.pi.lux.repository.ProdutoRepository;

import java.util.Base64;
import java.util.List;

@Controller
public class ListarProdutoController {

    @Autowired
    private ProdutoRepository repository;

    private static final int PRODUTOS_POR_PAGINA = 10;

    @GetMapping("/listarProd")
    public String listarProdutos(
            @RequestParam(defaultValue = "0") int pagina,
            Model model,
            @RequestParam(required = false) String nome) {

        Pageable pageable = PageRequest.of(pagina, PRODUTOS_POR_PAGINA, Sort.by(Sort.Order.desc("data")));
        Page<Produto> produtosPage;

        if (nome != null && !nome.isEmpty()) {
            produtosPage = repository.findByNomeContainingIgnoreCase(nome, pageable);
        } else {
            produtosPage = repository.findAll(pageable);
        }

        // Adicionando a imagem codificada em Base64
        produtosPage.getContent().forEach(produto -> {
            if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
                ProdutoImagem imagemPrincipal = produto.getImagemPrincipal();
                if (imagemPrincipal != null && imagemPrincipal.getImagem() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(imagemPrincipal.getImagem());
                    produto.setBase64Image(base64Image);
                }
            }
        });

        model.addAttribute("produtos", produtosPage.getContent());
        model.addAttribute("paginaAtual", pagina);
        model.addAttribute("totalPaginas", produtosPage.getTotalPages());
        model.addAttribute("totalProdutos", produtosPage.getTotalElements());

        return "listarProd";
    }

    @GetMapping("/pesquisarProduto")
    public String pesquisarProduto(@RequestParam("nomeProduto") String nomeProduto, Model model) {
        List<Produto> produtosEncontrados = repository.findByNomeContainingIgnoreCase(nomeProduto);

        // Codifica as imagens em Base64 para os produtos encontrados
        produtosEncontrados.forEach(produto -> {
            if (produto.getImagens() != null && !produto.getImagens().isEmpty()) {
                ProdutoImagem imagemPrincipal = produto.getImagemPrincipal();
                if (imagemPrincipal != null && imagemPrincipal.getImagem() != null) {
                    String base64Image = Base64.getEncoder().encodeToString(imagemPrincipal.getImagem());
                    produto.setBase64Image(base64Image);
                }
            }
        });
        model.addAttribute("produtos", produtosEncontrados);

        return "homeEcommerce";
    }

}
